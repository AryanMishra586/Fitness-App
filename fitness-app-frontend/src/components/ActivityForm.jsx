import { Button, MenuItem } from '@mui/material';
import React from 'react';
import { useState } from 'react';
import { Box, FormControl, InputLabel, Select, TextField } from '@mui/material';
import { useDispatch } from 'react-redux';
import { useSelector } from 'react-redux';
import { useEffect } from 'react';



const ActivityForm = ({onActivitysAdded}) => {

  const [activity, setActivity] = useState({ type : "RUNNING", duration : '', caloriesBurned : ''});

  const handleSubmit = async (e) =>{
    e.preventDefault();
    try{
      // await addActivity(activity);
      onActivitysAdded();
      setActivity({ type : "RUNNING", duration : '', caloriesBurned : ''});

    }
    catch(error){

    }
  }
  return (
     <Box component="form" onSubmit={handleSubmit} sx={{ mb : 4 }}>
      <FormControl fullWidth sx={{ mb : 2 }}>
        <InputLabel>Activity Type</InputLabel>
        <Select value={activity.type} onChange={(e) => setActivity({...activity, type: e.target.value})}>
          <MenuItem value="RUNNING">Running</MenuItem>
          <MenuItem value="CYCLING">Cycling</MenuItem>
          <MenuItem value="WALKING">Walking</MenuItem>
        </Select>
      </FormControl>
      <TextField  fullWidth label="Duration (minutes)" type='number' sx={{ mb : 2}} value={activity.duration} onChange={(e) => setActivity({...activity, duration: e.target.value})} />
      <TextField  fullWidth label="Calories Burned" type='number' sx={{mb : 2}} value={activity.caloriesBurned} onChange={(e) => setActivity({...activity, caloriesBurned: e.target.value})} />
        <Button type="submit" variant="contained" color="primary">
          Add Activity
        </Button>
     </Box>
  );
}
    

export default ActivityForm;