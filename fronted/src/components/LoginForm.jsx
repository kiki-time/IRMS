import React, { useState } from "react";
import { loginUser } from "../services/api";
import { saveToken } from "../auth/jwtUtils";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
const LoginForm = () => {
  const [form, setForm] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const res = await loginUser(form);
      console.log("로그인 응답:", res.data);
      const token = res.data.token;
      if (!token) {
        throw new Error("토큰이 전달되지 않았습니다.");
      }
      saveToken(token);
      navigate("/dashboard");
    } catch (err) {
      console.error("로그인 에러:", err);
      setError("로그인 실패: 사용자 정보를 확인해주세요.");
    }
  };

  return (
    <div className="container">
      <div className="form-box">
        <h2>로그인</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <label>아이디</label>
            <input
              type="text"
              name="username"
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-row">
            <label>비밀번호</label>
            <input
              type="password"
              name="password"
              onChange={handleChange}
              required
            />
          </div>
          <button type="submit">로그인</button>
          <Link to="/register">
            <button>회원가입</button>
          </Link>
        </form>
        {error && <div className="result-msg result-fail">{error}</div>}
      </div>
    </div>
  );
};

export default LoginForm;
