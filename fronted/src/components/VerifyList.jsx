import React, { useEffect, useState } from "react";
import { getReceivedEnvelopes, getSentEnvelopes } from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/styles.css";

export default function VerifyList() {
  const [received, setReceived] = useState([]);
  const [sent, setSent] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([getReceivedEnvelopes(), getSentEnvelopes()])
      .then(([recvRes, sentRes]) => {
        setReceived(recvRes.data);
        setSent(sentRes.data);
      })
      .catch(() => {
        setError("봉투 목록을 불러오는 데 실패했습니다.");
      });
  }, []);

  return (
    <div className="container">
      <div className="form-box">
        <h2>전자봉투 목록</h2>
        {error && <div className="result-msg result-fail">{error}</div>}

        <div className="form-content">
          <h3>📬 받은 봉투</h3>
          {received.length === 0 ? (
            <p>받은 봉투가 없습니다.</p>
          ) : (
            <ul className="envelope-list">
              {received.map((env) => (
                <li key={env.id}>
                  {env.originalFileName} (ID: {env.id})
                  <span className="sender">
                    - 보낸 사람: {env.uploaderUsername}
                  </span>
                  <button onClick={() => navigate(`/verify/${env.id}`)}>
                    열기
                  </button>
                </li>
              ))}
            </ul>
          )}

          <h3>📤 보낸 봉투</h3>
          {sent.length === 0 ? (
            <p>보낸 봉투가 없습니다.</p>
          ) : (
            <ul className="envelope-list">
              {sent.map((env) => (
                <li key={env.id}>
                  {env.originalFileName} (ID: {env.id})
                  <span className="sender">
                    - 받은 사람: {env.receiverUsername}
                  </span>
                  <button onClick={() => navigate(`/verify/${env.id}`)}>
                    열기
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
