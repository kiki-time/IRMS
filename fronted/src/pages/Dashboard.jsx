import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/styles.css";

export default function Dashboard() {
  const navigate = useNavigate();

  return (
    <div className="container">
      <div className="form-box dashboard-menu">
        <h2>
          <span role="img" aria-label="menu">
            📋
          </span>
          IRMS 기능 메뉴
        </h2>
        <div className="tab-buttons">
          <button onClick={() => navigate("/upload")}>
            <span role="img" aria-label="created">
              📥
            </span>
            전자봉투 생성
          </button>
          <button onClick={() => navigate("/verify")}>
            <span role="img" aria-label="download">
              🔍
            </span>
            전자봉투 목록
          </button>
          <button onClick={() => navigate("/mypage")}>
            <span role="img" aria-label="mypage">
              👤
            </span>
            마이페이지 (보낸/받은 봉투)
          </button>
        </div>
      </div>
    </div>
  );
}
