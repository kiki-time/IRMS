import React, { useState } from "react";
import { uploadEnvelope } from "../services/api";
import "../styles/styles.css";

export default function UploadEnvelope() {
  const [selectedFile, setSelectedFile] = useState(null);
  const [result, setResult] = useState({ status: "", message: "" });

  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
    setResult({ status: "", message: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedFile) {
      setResult({ status: "fail", message: "파일을 선택해주세요." });
      return;
    }

    try {
      const response = await uploadEnvelope(selectedFile);
      setResult({ status: "success", message: response.data });
      setSelectedFile(null);
    } catch (error) {
      const message = error.response?.data || "❌ 업로드 실패 ";
      setResult({ status: "fail", message });
    }
  };

  return (
    <div className="container">
      <div className="form-box">
        <h2>전자봉투 업로드</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <label htmlFor="file">파일 선택</label>
            <input type="file" id="file" onChange={handleFileChange} />
          </div>
          <button type="submit">업로드 및 암호화</button>
        </form>
        {result.message && (
          <div
            className={`result-msg result-${
              result.status === "success" ? "success" : "fail"
            }`}
          >
            {result.message}
          </div>
        )}
      </div>
    </div>
  );
}
