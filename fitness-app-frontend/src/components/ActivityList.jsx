import React, {useState} from 'react';
import { Card, CardContent, Typography } from '@mui/material';
import { useNavigate } from 'react-router';
import { useSelector } from 'react-redux';
import Grid from "@mui/material/Grid2";
import { setActivities } from '../store/activitySlice';
import { getActivities } from '../services/api.js';
import { useEffect } from 'react';

const ActivityList = () => {

  const [activities, setActivities] = useState([]);
  const navigate = useNavigate();
  const userId = useSelector((state) => state.auth.userId);

  const fetchActivities = async () =>{
    try{
      const response = await getActivities(userId);
      setActivities(response.data);
    }
    catch(error){
      console.error("Error fetching activities:", error);
    }
  }

  useEffect(() => {
    fetchActivities();
  }, [userId]);

   return (
    <Grid container spacing={2}>
      {activities.map((activity) => (
        <Grid
          key={activity.id}
          size={{ xs: 12, sm: 6, md: 4 }}
        >
          <Card
            sx={{ cursor: "pointer" }}
            onClick={() => navigate(`/activities/${activity.id}`)}
          >
            <CardContent>
              <Typography variant="h6">
                {activity.type}
              </Typography>

              <Typography>
                Duration: {activity.duration}
              </Typography>

              <Typography>
                Calories: {activity.caloriesBurned}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
};

export default ActivityList;