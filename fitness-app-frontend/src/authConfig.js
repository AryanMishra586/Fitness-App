export const authConfig = {
  clientId: "oauth2-pkce-client",

  authorizationEndpoint:
    "http://localhost:8181/realms/fitness-outh2/protocol/openid-connect/auth",

  tokenEndpoint:
    "http://localhost:8181/realms/fitness-outh2/protocol/openid-connect/token",

  logoutEndpoint:
    "http://localhost:8181/realms/fitness-outh2/protocol/openid-connect/logout",

  redirectUri: "http://localhost:5173/",

  scope: "openid profile email",

  onRefreshTokenExpire: (event) => event.logIn(),
};