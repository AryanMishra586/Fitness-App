import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router";
import { Button, Box, Typography } from "@mui/material";
import { useContext, useEffect, useState } from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";
import { setCredentials } from "./store/authSlice";

import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";

const ActivitiesPage = () => {
  return (
    <Box sx={{ p: 3 }}>
      <ActivityForm
        onActivitysAdded={() => window.location.reload()}
      />
      <ActivityList />
    </Box>
  );
};

function App() {
  const { token, tokenData, login, logOut } =
    useContext(AuthContext);
    // console.log(useContext(AuthContext));

  const dispatch = useDispatch();

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }));
    }
  }, [token, tokenData, dispatch]);

  return (
    <Router>
      {!token ? (
        <Box
          sx={{
            height: "100vh",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            flexDirection: "column",
            gap: 2,
          }}
        >
          <Typography variant="h3" fontWeight="bold">
            Fitness Activity Tracker
          </Typography>

          <Typography variant="h6" color="text.secondary">
            Track your workouts and receive AI-powered fitness recommendations.
          </Typography>

          <Typography color="error">
            Test Credentials
          </Typography>

          <Typography>
            <b>Username:</b> user1
          </Typography>

          <Typography>
            <b>Password:</b> password1
          </Typography>

          <Button
          variant="contained"
          color="primary"
          size="large"
          onClick={() => login()}
            >
          LOGIN
          </Button>
        </Box>
      ) : (
        <Box sx={{ p: 2 }}>
          <Box
            sx={{
              display: "flex",
              justifyContent: "flex-end",
              mb: 2,
            }}
          >
            <Button
  variant="outlined"
  color="error"
  onClick={() => logOut()}
>
  Logout
</Button>
          </Box>

          <Routes>
            <Route
              path="/activities"
              element={<ActivitiesPage />}
            />

            <Route
              path="/activities/:id"
              element={<ActivityDetail />}
            />

            <Route
              path="/"
              element={<Navigate to="/activities" replace />}
            />
          </Routes>
        </Box>
      )}
    </Router>
  );
}

export default App;