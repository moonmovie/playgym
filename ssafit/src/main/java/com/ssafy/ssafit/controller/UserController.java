package com.ssafy.ssafit.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.ssafit.domain.ApiResMessage;
import com.ssafy.ssafit.domain.Characters;
import com.ssafy.ssafit.domain.GetCt;
import com.ssafy.ssafit.domain.MainUser;
import com.ssafy.ssafit.domain.SubUser;
import com.ssafy.ssafit.dto.SubUserDto;
import com.ssafy.ssafit.repository.MainuserRepository;
import com.ssafy.ssafit.security.JwtTokenProvider;
import com.ssafy.ssafit.service.MainUserService;
import com.ssafy.ssafit.service.SubUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
	
	private final PasswordEncoder passwordEncoder;	
	private final JwtTokenProvider jwtTokenProvider;
	private final MainuserRepository userRepository;
	private final MainUserService mainUserService;
	private final SubUserService subUserService;
	
	@PostMapping("/join")
	public ResponseEntity<ApiResMessage> join(@RequestBody Map<String, String> user) {
		try {
			String password = user.get("password");
			if(!Pattern.matches("^((?=.*[A-Za-z])(?=.*[0-9])(?=.*[`~!@#$%^&*\\-\\+\\=\\?.,<>\\[\\]\\{\\}\\;\\:\\'\\\"])).{8,20}$", password))
				return new ResponseEntity<ApiResMessage>(new ApiResMessage(500,null,"Invaildate password"),HttpStatus.INTERNAL_SERVER_ERROR);
				userRepository.save(MainUser.builder()
	                .userId(user.get("userid"))
					.email(user.get("email"))
	                .password(passwordEncoder.encode(password))
	                .name(user.get("name"))
	                .phone(user.get("phone"))
	                .roles(Collections.singletonList("ROLE_USER")) //
	                .build()).getId();
		} catch(ConstraintViolationException e) {
			return new ResponseEntity<ApiResMessage>(new ApiResMessage(500,null,"propertyNull"),HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (DataIntegrityViolationException e) {
			return new ResponseEntity<ApiResMessage>(new ApiResMessage(500,null,"existId"),HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(Exception e) {
			return new ResponseEntity<ApiResMessage>(new ApiResMessage(500,null,"Faild"),HttpStatus.INTERNAL_SERVER_ERROR);
		} 
		return new ResponseEntity<ApiResMessage>(new ApiResMessage(200,null,"??????????????? ?????????????????????."),HttpStatus.OK);
	}
	
	 // ?????????
    @PostMapping("/login")
    public ResponseEntity<ApiResMessage> login(@RequestBody Map<String, String> user) {
    	MainUser member=null;
    	 Map<String, Object> userinfo = new HashMap<String, Object>();
    	try {
    		member = userRepository.findByUserId(user.get("userid")).orElse(null);
    		if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            	return new ResponseEntity<ApiResMessage>(new ApiResMessage(401,null,"Failed"),HttpStatus.UNAUTHORIZED);
            }
    	        userinfo.put("id", member.getId());
    	        userinfo.put("userId", member.getUserId());
    	        userinfo.put("email", member.getEmail());
    	        userinfo.put("name", member.getName());
    	        userinfo.put("phone", member.getPhone());
    	        userinfo.put("token", jwtTokenProvider.createToken(member.getUsername(), member.getRoles()));
    	       
    	        userinfo.put("child", mainUserService.loginService(member));
    	}catch(Exception e){
    		return new ResponseEntity<ApiResMessage>(new ApiResMessage(500,null,"Failed"),HttpStatus.INTERNAL_SERVER_ERROR);
    	}
        
        return new ResponseEntity<ApiResMessage>(new ApiResMessage(200,userinfo,"Success"),HttpStatus.OK);
    }
    
    @GetMapping("/check")
    public ResponseEntity<ApiResMessage> IdCheck(@RequestParam String id){
    	if(!mainUserService.existId(id)) {
    		return new ResponseEntity<ApiResMessage>(new ApiResMessage(401,null,"existId"),HttpStatus.UNAUTHORIZED);
    	}
    	return new ResponseEntity<ApiResMessage>(new ApiResMessage(200,null,"Success"),HttpStatus.OK);
    }
    
    //???????????? ?????? ?????? ??????
    @PostMapping("/user/checkPw")
    public boolean checkPw(@RequestBody Map<String, String> user) throws Exception {
    	MainUser member = userRepository.findByUserId(user.get("userid"))
                .orElseThrow(() -> new IllegalArgumentException("?????? ????????? ????????????."));
    	System.out.println(member);
    	boolean result = false;
    	if(passwordEncoder.matches(user.get("password"), member.getPassword())){
    		result = true;
    	}else {
    		result = false;
    	}
    	return result;
    }
    //?????? ?????? ??????
    @GetMapping("/user/search")
    public Map<String, Object> findUser(@RequestParam Long id) {
    	Map<String, Object> obj = null;
    	List<Map<String, Object>> result = null;
    	try {
    		result = subUserService.getMySubUserList(id);
    		Optional<MainUser> user = userRepository.findById(id);
    		if(user != null) {
    			obj = new HashMap<String, Object>();
    			MainUser mUser = user.get();
    			obj.put("id", mUser.getId());
    			obj.put("userid", mUser.getUserId());
    			obj.put("password", mUser.getPassword());
    			obj.put("email", mUser.getEmail());
    			obj.put("name", mUser.getName());
    			obj.put("phone", mUser.getPhone());
    			obj.put("subUsers", result);
    		}
    	}
    	catch (Exception e) {
    		throw e;
    	}
    	return obj;
    }
    
    // ?????? ?????? ??????
    @PutMapping("/user/update")
    public Optional<MainUser> update(
    		@RequestParam Long id,
    		@RequestBody MainUser user){
    	Optional<MainUser> updateUser = userRepository.findById(id);
    	
    	updateUser.ifPresent(selectUser -> {
    		selectUser.setName(user.getName());
    		selectUser.setPassword(passwordEncoder.encode(user.getPassword()));
    		selectUser.setPhone(user.getPhone());
    		selectUser.setEmail(user.getEmail());
    		
    		userRepository.save(selectUser);
    	});
    	return updateUser;
    }
	
    

	@DeleteMapping("/user/delete")
	public ResponseEntity<ApiResMessage> deleteMember(@RequestParam long id){
		try {
			mainUserService.deleteMember(id);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<ApiResMessage>(new ApiResMessage(500,null,"Deleted Error"),HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		return new ResponseEntity<ApiResMessage>(new ApiResMessage(200,null,"OK"),HttpStatus.OK);
		
	}
}
