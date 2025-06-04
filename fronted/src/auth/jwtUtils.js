export const getToken = () => localStorage.getItem("token");

export const saveToken = (token) => localStorage.setItem("token", token);

export const removeToken = () => localStorage.removeItem("token");

export const isAuthenticated = () => !!getToken();

export const getUsernameFromToken = () => {
  const token = getToken();
  if (!token) return null;
  const payload = token.split(".")[1];
  try {
    return JSON.parse(atob(payload)).sub;
  } catch {
    return null;
  }
};
