import React, { useEffect, useState } from "react";
import { useParams } from "react-router";
import {
  Card,
  CardContent,
  Typography,
  Box,
  Divider,
} from "@mui/material";
import { getActivityDetail } from "../services/api.js";

const ActivityDetail = () => {
  const { id } = useParams();
  const [recommendation, setRecommendation] = useState(null);

  useEffect(() => {
    const fetchActivityDetail = async () => {
      try {
        const response = await getActivityDetail(id);

        console.log(response.data);

        setRecommendation(response.data);
      } catch (error) {
        console.error("Error fetching activity detail:", error);
      }
    };

    fetchActivityDetail();
  }, [id]);

  if (!recommendation) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Box sx={{ maxWidth: 800, mx: "auto", p: 2 }}>
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Activity Details
          </Typography>

          <Typography>
            Activity Type: {recommendation.activityType}
          </Typography>

          <Typography>
            Activity ID: {recommendation.activityId}
          </Typography>

          <Typography>
            Generated On:{" "}
            {recommendation.createdAt
              ? new Date(recommendation.createdAt).toLocaleString()
              : "N/A"}
          </Typography>
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            AI Recommendation
          </Typography>

          <Typography variant="h6">
            Analysis
          </Typography>

          <Typography paragraph>
            {recommendation.recommendation ||
              "Unable to generate recommendation due to an AI service error."}
          </Typography>

          <Divider sx={{ my: 2 }} />

          <Typography variant="h6">
            Improvements
          </Typography>

          {recommendation.improvements?.length > 0 ? (
            recommendation.improvements.map((item, index) => (
              <Typography key={index} paragraph>
                • {item}
              </Typography>
            ))
          ) : (
            <Typography>No improvements available.</Typography>
          )}

          <Divider sx={{ my: 2 }} />

          <Typography variant="h6">
            Suggestions
          </Typography>

          {recommendation.suggestions?.length > 0 ? (
            recommendation.suggestions.map((item, index) => (
              <Typography key={index} paragraph>
                • {item}
              </Typography>
            ))
          ) : (
            <Typography>No suggestions available.</Typography>
          )}

          <Divider sx={{ my: 2 }} />

          <Typography variant="h6">
            Safety Guidelines
          </Typography>

          {recommendation.safety?.length > 0 ? (
            recommendation.safety.map((item, index) => (
              <Typography key={index} paragraph>
                • {item}
              </Typography>
            ))
          ) : (
            <Typography>No safety guidelines available.</Typography>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default ActivityDetail;