import React from "react";
import { useNavigate } from "react-router-dom";
import { removeToken, getUsernameFromToken } from "../auth/jwtUtils";
import { Link } from "react-router-dom";

const Topbar = () => {
  const navigate = useNavigate();
  const username = getUsernameFromToken();

  const handleLogout = () => {
    removeToken();
    navigate("/login");
  };

  return (
    <div className="topbar">
      <h2>
        <span role="img" aria-label="sending">
          📪
        </span>
        IRMS: 전자봉투 보안 시스템
      </h2>
      <div className="topbar-right">
        <Link to="/dashboard" className="nav-button">
          대시보드
        </Link>
        <Link to="/keys/generate" className="nav-button">
          키 생성
        </Link>

        <Link to="/upload" className="nav-button">
          전자봉투 생성
        </Link>
        <Link to="/verify" className="nav-button">
          전자봉투 목록
        </Link>
        <Link to="/mypage" className="nav-button">
          마이페이지
        </Link>
        <div className="username-display">
          <span role="img" aria-label="ppl">
            👤
          </span>
          {username}
        </div>
        <button onClick={handleLogout} className="logout-button">
          로그아웃
        </button>
      </div>
    </div>
  );
};

export default Topbar;
