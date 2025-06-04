import React, { useState, useEffect } from "react";
import {
  changePassword,
  transferEnvelope,
  searchUser,
  getCreatedEnvelopes,
  getSentEnvelopes,
  getReceivedEnvelopes,
} from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/styles.css";
import searchIcon from "../assets/search-icon.png";

export default function Mypage() {
  const [activeTab, setActiveTab] = useState("password");
  const [password, setPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [envelopeId, setEnvelopeId] = useState("");
  const [receiverId, setReceiverId] = useState("");
  const [userFullName, setUserFullName] = useState(null);
  const [result, setResult] = useState(null);
  const [envelopes, setEnvelopes] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    setResult(null);
    setUserFullName(null);

    const fetchData = async () => {
      try {
        if (activeTab === "transfer") {
          const res = await getCreatedEnvelopes();
          setEnvelopes(res.data);
        } else if (activeTab === "sent") {
          const res = await getSentEnvelopes();
          setEnvelopes(res.data);
        } else if (activeTab === "received") {
          const res = await getReceivedEnvelopes();
          setEnvelopes(res.data);
        }
      } catch (err) {
        setResult({ success: false, message: "데이터 로딩 실패" });
      }
    };

    fetchData();
  }, [activeTab]);

  const handlePasswordChange = async () => {
    if (newPassword !== confirmPassword) {
      setResult({ success: false, message: "비밀번호가 일치하지 않습니다." });
      return;
    }

    try {
      await changePassword({ password, newPassword });
      setResult({ success: true, message: "비밀번호 변경 완료" });
      setPassword("");
      setNewPassword("");
      setConfirmPassword("");
    } catch (err) {
      setResult({
        success: false,
        message: "비밀번호 변경 실패: " + err.response?.data,
      });
    }
  };

  const handleTransfer = async () => {
    try {
      const res = await transferEnvelope({
        envelopeId,
        receiverUsername: receiverId,
      });
      setResult({ success: true, message: res.data });
      setEnvelopeId("");
      setReceiverId("");
      setUserFullName(null);
    } catch (err) {
      setResult({
        success: false,
        message: "봉투 전달 실패: " + err.response?.data,
      });
    }
  };

  const handleUserSearch = async () => {
    if (receiverId.length === 0) return;
    try {
      const res = await searchUser(receiverId);
      const matched = res.data.find((name) => name === receiverId);
      if (matched) {
        setUserFullName(receiverId + " 님");
        setResult(null);
      } else {
        setUserFullName(null);
        setResult({
          success: false,
          message: "사용자를 찾을 수 없습니다. 올바른 ID를 입력해주세요.",
        });
      }
    } catch {
      setUserFullName(null);
      setResult({
        success: false,
        message: "사용자 검색 실패: 올바른 ID를 입력해주세요.",
      });
    }
  };

  return (
    <div className="container">
      <div
        className="form-box"
        style={{ display: "flex", flexDirection: "row", gap: "20px" }}
      >
        <div style={{ width: "150px" }}>
          <button
            onClick={() => setActiveTab("password")}
            className={activeTab === "password" ? "active" : ""}
          >
            비밀번호 변경
          </button>
          <button
            onClick={() => setActiveTab("transfer")}
            className={activeTab === "transfer" ? "active" : ""}
          >
            봉투 전달
          </button>
          <button
            onClick={() => setActiveTab("sent")}
            className={activeTab === "sent" ? "active" : ""}
          >
            보낸 봉투
          </button>
          <button
            onClick={() => setActiveTab("received")}
            className={activeTab === "received" ? "active" : ""}
          >
            받은 봉투
          </button>
        </div>

        <div style={{ flex: 1, maxHeight: "60vh", overflowY: "auto" }}>
          {activeTab === "password" && (
            <>
              <h2>비밀번호 변경</h2>
              <div className="form-row">
                <label>현재 비밀번호</label>
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
              </div>
              <div className="form-row">
                <label>새 비밀번호</label>
                <input
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                />
              </div>
              <div className="form-row">
                <label>새 비밀번호 확인</label>
                <input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                />
              </div>
              <button onClick={handlePasswordChange}>비밀번호 변경</button>
            </>
          )}

          {activeTab === "transfer" && (
            <>
              <h2>봉투 전달</h2>
              <div className="form-row">
                <label>봉투 선택</label>
                <select
                  value={envelopeId}
                  onChange={(e) => setEnvelopeId(e.target.value)}
                >
                  <option value="">-- 봉투를 선택하세요 --</option>
                  {envelopes.map((env) => (
                    <option key={env.id} value={env.id}>
                      {env.originalFileName} (ID: {env.id})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-row">
                <label>받을 사용자 ID</label>
                <div className="receiver-input-wrapper">
                  <input
                    type="text"
                    value={receiverId}
                    onChange={(e) => setReceiverId(e.target.value)}
                  />
                  <button onClick={handleUserSearch} title="사용자 확인">
                    <img src={searchIcon} alt="검색" width="16" />
                  </button>
                </div>
              </div>
              {userFullName && <p>✅ 확인되었습니다: {userFullName}</p>}
              <button
                onClick={handleTransfer}
                disabled={!envelopeId || !receiverId || !userFullName}
              >
                봉투 전달
              </button>
            </>
          )}

          {(activeTab === "sent" || activeTab === "received") && (
            <>
              <h2>{activeTab === "sent" ? "보낸 봉투" : "받은 봉투"}</h2>
              {envelopes.length === 0 ? (
                <p>📭 봉투가 없습니다.</p>
              ) : (
                <ul>
                  {envelopes.map((env) => (
                    <li key={env.id} className="open-btn">
                      📨 {env.originalFileName} (ID: {env.id})
                      {activeTab === "received" && (
                        <span
                          style={{ marginLeft: "10px", fontStyle: "italic" }}
                        >
                          - 보낸 사람: {env.uploaderUsername}
                        </span>
                      )}
                      <button
                        onClick={() => navigate(`/verify/${env.id}`)}
                        style={{ marginLeft: "10px" }}
                      >
                        열기
                      </button>
                    </li>
                  ))}
                </ul>
              )}
            </>
          )}

          {result && (
            <div
              className={`result-msg ${
                result.success ? "result-success" : "result-fail"
              }`}
            >
              {result.message}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
