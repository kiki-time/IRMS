import React from "react";
import { Link } from "react-router-dom";
import "../styles/styles.css";

export default function AuthHome() {
  return (
    <div className="container">
      <div className="form-box">
        <h2>전자봉투 시스템</h2>
        <p>시작하려면 아래에서 선택해주세요.</p>
        <div className="button-group">
          <Link to="/login">
            <button>로그인</button>
          </Link>
          <Link to="/register">
            <button>회원가입</button>
          </Link>
        </div>
      </div>
    </div>
  );
}
