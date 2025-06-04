import React, { useState } from "react";
import { registerUser } from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/styles.css";
import { Link } from "react-router-dom";

export default function RegisterForm() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    confirmPassword: "",
    fullName: "",
    email: "",
  });

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");

    try {
      const response = await registerUser(form);
      setMessage(response.data.message || "회원가입 성공");
      setTimeout(() => navigate("/login"), 1000);
    } catch (err) {
      const msg =
        err.response?.data?.message || "회원가입 실패. 입력값을 확인해주세요.";
      setError(msg);
    }
  };

  return (
    <div className="container">
      <div className="form-box">
        <h2>회원가입</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <label>아이디</label>
            <input
              type="text"
              name="username"
              value={form.username}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-row">
            <label>비밀번호</label>
            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-row">
            <label>비밀번호 확인</label>
            <input
              type="password"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-row">
            <label>이름</label>
            <input
              type="text"
              name="fullName"
              value={form.fullName}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-row">
            <label>이메일</label>
            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
            />
          </div>
          <button type="submit">회원가입</button>
          <Link to="/">
            <button>돌아가기</button>
          </Link>
        </form>
        {error && <div className="result-msg result-fail">{error}</div>}
        {message && <div className="result-msg result-success">{message}</div>}
      </div>
    </div>
  );
}
