import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 인증
export const loginUser = (credentials) => api.post("/auth/login", credentials);
export const registerUser = (userInfo) => api.post("/auth/register", userInfo);

// 키 쌍 생성
export const generateKeyPair = () => api.post("/keys/generate");

// 전자봉투 업로드
export const uploadEnvelope = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return api.post("/envelopes/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

// 전자봉투 목록
export const getCreatedEnvelopes = () => api.get("/envelopes/created");
export const getReceivedEnvelopes = () => api.get("/envelopes/received");
export const getSentEnvelopes = () => api.get("/envelopes/sent");

// 전자봉투 조회/검증/다운로드
export const getEnvelopeById = (id) => api.get(`/envelopes/${id}`);
export const downloadEnvelope = (id) =>
  api.get(`/envelopes/download/${id}`, { responseType: "blob" });
export const verifyEnvelope = (id) => api.get(`/envelopes/verify/${id}`);

export const fetchReceivedEnvelopes = () => {
  return api.get("/envelopes/received");
};
// 전자봉투 전달
export const transferEnvelope = (data) => api.post("/envelopes/transfer", data);

// 비밀번호 변경
export const changePassword = (data) => api.post("/auth/change-password", data);

// 사용자 검색
export const searchUser = (keyword) => {
  return api.get(`/envelopes/search-user?keyword=${keyword}`);
};
// fullname 조회
export const getUserFullName = (username) => {
  return api.get(`/users/fullname/${username}`);
};

export default api;
