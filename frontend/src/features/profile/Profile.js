import React, { useEffect, useState } from 'react';
import { Route, Link, NavLink } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import styles from './Profile.module.css'
import Grid from '@mui/material/Grid'
import Tooltip from '@mui/material/Tooltip'
import IconButton from '@mui/material/IconButton'
import AddIcon from '@mui/icons-material/Add'
import EditIcon from '@mui/icons-material/Edit';
import GavelIcon from '@mui/icons-material/Gavel';
import ChildCareIcon from '@mui/icons-material/ChildCare';
import AddProfileDialog from './AddProfileDialog'
import EditMainUserInfoDialog from './EditMainUserInfoDialog'
import { StatDialog } from './StatDialog'
import { SmallDarkButton, EditButton } from './customProfileStyle'
import { BackAnimation } from '../login/styledComponent'
import cloudImage from '../../images/background_cloud.png'
import selectPlayer from '../../images/selectPlayer.png'
import bear from '../../images/characters/bear.png'
import cat from '../../images/characters/cat.png'
import chick from '../../images/characters/chick.png'
import rabbit from '../../images/characters/rabbit.png'
import {requestGetChildren} from '../../app/actions/userActions'
import Logout from '../logout/Logout'

function Player({player}){
  const selectPlayer=()=>{
    // console.log(player)
    localStorage.setItem('sub-user', player.sid)
    // console.log(localStorage.getItem('sub-user'))
    window.location = '/home'
  }
  const onEditPlayerClick = (e) => {
    console.log('edit?')
    e.stopPropagation()
    // console.log(player)
  }
  return(
    <div>
      <Grid item onClick={selectPlayer}>
        <div className={styles.player}>
          <img src={player.image} width='100px'/>
        </div>

        <EditButton onClick={onEditPlayerClick}><GavelIcon fontSize="small"/></EditButton>
        {player.nickName}
      </Grid>
    </div>
  )
}

export function Profile(){
  const players = useSelector(state=>state.user.subUser)
  const dispatch = useDispatch()
  // const [players, setPlayers] = useState([])
  // useEffect(()=>{
  //   dispatch(requestGetChildren(localStorage.getItem('main-user')))
  //     // .then(res => {
  //       // console.log(res)
  //       // setPlayers(res.payload)
  //     // })
  // },[])

  useEffect(()=>{
    console.log(players) 
    // console.log(test)
  },[players])
  
  // add profile click -> open dialog
  const [addOpen, setAddOpen] = useState(false);
  const onAddProfileClick = () => {
    setAddOpen(true)
  }

  const onAddProfileClose = () => {
    setAddOpen(false);
  };

  // main user info edit dialog
  
  const [editOpen, setEditOpen] = useState(false);
  const onEditProfileClick = () => {
    setEditOpen(true)
  }

  const onEditProfileClose = () => {
    setEditOpen(false);
  };

  const [statOpen, setStatOpen] = useState(false);
  const onStatClick = () => {
    setStatOpen(true)
  }

  const onStatClose = () => {
    setStatOpen(false)
  }

  return(
    <div className={styles.profile_container}>
      <Logout />
      <BackAnimation 
        animate={{ 
          scale: 0, 
          x:-100, 
          y:-100,
          position: 'fixed',
          top:'-50px',
          left:'-100px'
        }}
        transition={{ duration: 1.2 }}
      >
        <img src={cloudImage} style={{width:'500px'}}/>
      </BackAnimation>
      <BackAnimation 
        animate={{ 
          scale: 0, 
          x:100, 
          y:-100,
          position: 'fixed',
          top:'-50px',
          left:'90%'
        }}
        transition={{ duration: 1.2 }}
      >
        <img src={cloudImage} style={{width:'500px'}}/>
      </BackAnimation>
      <BackAnimation 
        animate={{ 
          scale: 0, 
          x:-100, 
          y:100,
          position: 'fixed',
          top:'80%',
          left:'-100px'
        }}
        transition={{ duration: 1.2 }}
      >
        <img src={cloudImage} style={{width:'500px'}}/>
      </BackAnimation>
      <BackAnimation 
        animate={{ 
          scale: 0, 
          x:100, 
          y:100,
          position: 'fixed',
          top:'80%',
          left:'90%'
        }}
        transition={{ duration: 1.2 }}
      >
        <img src={cloudImage} style={{width:'500px'}}/>
      </BackAnimation>
      <Grid 
        container
        direction="column"
        justifyContent="center"
        alignItems="center"
      >
        <Grid item mt={'10%'} mb={'2%'}>
          <img src={selectPlayer} width='600px'/>
        </Grid>

        <Grid item>
          <Grid
            container
            direction="row"
            justifyContent="center"
            alignItems="center"
            spacing={3}
          >
            {players.map(item=>(<Player key={item.sid} player={item}/>))}

            <Tooltip title="Add Player">
              <IconButton onClick={onAddProfileClick}>
                <AddIcon />
              </IconButton>
            </Tooltip>
          </Grid>
        </Grid> 

        <Grid item width="100vh" mt={'10%'}>
          <Grid
            container
            direction="row"
            justifyContent="flex-end"
            alignItems="center"
          >
            <SmallDarkButton onClick={onEditProfileClick}>
              나의 정보 <EditIcon />
            </SmallDarkButton>
            <SmallDarkButton onClick={onStatClick}>
              플레이어 현황 <ChildCareIcon />
            </SmallDarkButton>
          </Grid>
        </Grid>
      </Grid>
      <AddProfileDialog open={addOpen} getClose={onAddProfileClose} />
      <EditMainUserInfoDialog open={editOpen} getClose={onEditProfileClose} />
      <StatDialog open={statOpen} getClose={onStatClose} />
    </div>
  )
}