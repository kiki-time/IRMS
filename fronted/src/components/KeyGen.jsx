import React, { useState } from "react";
import axios from "axios";
import { getToken } from "../auth/jwtUtils";

export default function KeyGen() {
  const [result, setResult] = useState("");

  const handleGenerate = async () => {
    try {
      const res = await axios.post("/api/keys/generate", null, {
        headers: {
          Authorization: `Bearer ${getToken()}`,
        },
      });
      setResult("✅ " + res.data);
    } catch (err) {
      if (err.response?.status === 400) {
        setResult("⚠️ " + err.response.data); // "이미 키가 존재합니다."
      } else {
        setResult("❌ 키 생성 실패: " + (err.response?.data || err.message));
      }
    }
  };

  return (
    <div className="container">
      <div className="form-box">
        <h2>RSA 키쌍 생성</h2>
        <button onClick={handleGenerate}>
          <span role="img" aria-label="keyGen">
            🔐{" "}
          </span>
          키쌍 생성
        </button>
        {result && (
          <div
            className={`result-msg ${
              result.startsWith("✅") ? "result-success" : "result-fail"
            }`}
          >
            {result}
          </div>
        )}
      </div>
    </div>
  );
}
