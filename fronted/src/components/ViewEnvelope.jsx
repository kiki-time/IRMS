import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
  downloadEnvelope,
  verifyEnvelope,
  getCreatedEnvelopes,
  getReceivedEnvelopes,
  getSentEnvelopes,
} from "../services/api";
import "../styles/styles.css";

export default function ViewEnvelope() {
  const { id } = useParams();

  const [envelope, setEnvelope] = useState(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (!id) return;

    Promise.all([
      getReceivedEnvelopes(),
      getCreatedEnvelopes(),
      getSentEnvelopes(),
    ])
      .then(([receivedRes, createdRes, sentRes]) => {
        const all = [...receivedRes.data, ...createdRes.data, ...sentRes.data];
        const matched = all.find((env) => String(env.id) === String(id));
        if (!matched) {
          setError("해당 전자봉투에 접근 권한이 없습니다.");
          return;
        }

        // 파일 다운로드
        downloadEnvelope(id)
          .then((response) => {
            const filename =
              response.headers["content-disposition"]
                ?.split('filename="')[1]
                ?.split('"')[0] || "다운로드된 파일";

            setEnvelope({
              id,
              filename,
              blob: response.data,
            });
          })
          .catch(() => {
            setError("봉투 정보를 가져오는 중 오류 발생");
          });
      })
      .catch(() => {
        setError("전자봉투 목록을 불러오는 데 실패했습니다.");
      });
  }, [id]);

  const handleVerify = () => {
    verifyEnvelope(id)
      .then((res) => {
        const decryptedContent = res.data;

        // 텍스트를 Blob으로 만들고 다운로드
        const blob = new Blob([decryptedContent], {
          type: "text/plain;charset=utf-8",
        });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute(
          "download",
          envelope?.filename || "복호화된_파일.txt"
        );
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        setMessage("✅ 서명 검증 및 복호화 성공. 파일을 다운로드했습니다.");
        setError("");
      })
      .catch((err) => {
        setMessage("");
        setError(err.response?.data || "검증 실패");
      });
  };

  const handleDownload = () => {
    const url = window.URL.createObjectURL(new Blob([envelope.blob]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", envelope.filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="container">
      <div className="form-box">
        <h2>전자봉투 보기</h2>

        {error && <div className="result-msg result-fail">{error}</div>}

        {!error && envelope ? (
          <>
            <p>
              <strong>파일 이름:</strong> {envelope.filename}
            </p>
            <button onClick={handleDownload}>
              <span role="img" aria-label="download">
                📥
              </span>{" "}
              다운로드
            </button>
            <button onClick={handleVerify}>
              <span role="img" aria-label="verify">
                🔍
              </span>{" "}
              서명 검증 및 복호화
            </button>
          </>
        ) : !error ? (
          <p>📦 봉투 정보를 불러오는 중...</p>
        ) : null}

        {message && <div className="result-msg result-success">{message}</div>}
      </div>
    </div>
  );
}
