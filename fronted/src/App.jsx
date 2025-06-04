import React, { useState, useEffect } from "react";
import { Routes, Route, Navigate, useLocation } from "react-router-dom";

import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import Dashboard from "./pages/Dashboard";
import Mypage from "./pages/Mypage";
import UploadEnvelope from "./components/UploadEnvelope";
import ViewEnvelope from "./components/ViewEnvelope";
import Topbar from "./components/Topbar";
import AuthHome from "./pages/AuthHome";
import { getToken } from "./auth/jwtUtils";
import KeyGen from "./components/KeyGen";
import VerifyList from "./components/VerifyList";

export default function App() {
  const location = useLocation();
  const [isAuthenticated, setIsAuthenticated] = useState(!!getToken());
  useEffect(() => {
    const checkToken = () => {
      const token = getToken();
      setIsAuthenticated(!!token);
    };

    checkToken();
    window.addEventListener("storage", checkToken);

    return () => window.removeEventListener("storage", checkToken);
  }, []);

  const hideTopbarPaths = ["/", "/login", "/register"];
  const shouldShowTopbar =
    isAuthenticated && !hideTopbarPaths.includes(location.pathname);

  return (
    <>
      {shouldShowTopbar && <Topbar />}
      <Routes>
        <Route path="/" element={<AuthHome />} />
        <Route path="/login" element={<LoginForm />} />
        <Route path="/register" element={<RegisterForm />} />
        <Route
          path="/dashboard"
          element={isAuthenticated ? <Dashboard /> : <Navigate to="/login" />}
        />
        <Route
          path="/upload"
          element={
            isAuthenticated ? <UploadEnvelope /> : <Navigate to="/login" />
          }
        />
        <Route
          path="/verify"
          element={isAuthenticated ? <VerifyList /> : <Navigate to="/login" />}
        />
        <Route
          path="/verify/:id"
          element={
            isAuthenticated ? <ViewEnvelope /> : <Navigate to="/login" />
          }
        />
        <Route
          path="/keys/generate"
          element={isAuthenticated ? <KeyGen /> : <Navigate to="/login" />}
        />
        <Route
          path="/mypage"
          element={isAuthenticated ? <Mypage /> : <Navigate to="/login" />}
        />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </>
  );
}
