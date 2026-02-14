import React, { useEffect, useState } from "react";
import axios from "axios";
import "./ResumeUpload.css";

const API_BASE = process.env.REACT_APP_API_BASE;

function ResumeUpload() {
  const [file, setFile] = useState(null);
  const [role, setRole] = useState("");
  const [experience, setExperience] = useState("");
  const [roles, setRoles] = useState([]);
  const [result, setResult] = useState(null);
  const [animatedScore, setAnimatedScore] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // ================= FETCH ROLES =================
  useEffect(() => {
    axios
      .get(`${API_BASE}/api/resume/roles`)
      .then((res) => setRoles(res.data))
      .catch((err) => {
        console.error(err);
        setError("Failed to load roles");
      });
  }, []);

  // ================= ANIMATE SCORE =================
  useEffect(() => {
    if (result?.score >= 0) {
      setAnimatedScore(0);
      setTimeout(() => {
        setAnimatedScore(result.score);
      }, 150);
    }
  }, [result]);

  // ================= HANDLE SUBMIT =================
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!file || !role) {
      alert("Please upload resume and select role");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("role", role);
    formData.append("experience", experience || 0);

    try {
      setLoading(true);
      setResult(null);
      setError("");

      const res = await axios.post(
        `${API_BASE}/api/resume/upload`,
        formData
      );

      setResult(res.data);
    } catch (err) {
      console.error(err);
      setError("Resume analysis failed");
    } finally {
      setLoading(false);
    }
  };

  // ================= PROGRESS COLOR CLASS =================
  const getProgressClass = (score) => {
    if (score >= 70) return "good";
    if (score >= 40) return "avg";
    return "bad";
  };

  return (
    <div className="page">
      <div className="container">
        {/* ================= UPLOAD CARD ================= */}
        <div className="card upload-card">
          <h2>Resume Analyzer</h2>
          <p className="subtitle">
            Upload a resume to evaluate skill alignment
          </p>

          <form onSubmit={handleSubmit}>
            <input
              type="file"
              accept=".pdf,.docx"
              className="file-input"
              onChange={(e) => setFile(e.target.files[0])}
            />

            <select
              className="select"
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >
              <option value="">Select Role</option>
              {roles.map((r) => (
                <option key={r} value={r}>
                  {r}
                </option>
              ))}
            </select>

            <input
              type="number"
              className="input"
              placeholder="Experience (years)"
              value={experience}
              onChange={(e) => setExperience(e.target.value)}
            />

            <button className="btn" disabled={loading}>
              {loading ? "Analyzing..." : "Analyze Resume"}
            </button>
          </form>

          {error && <p className="error">{error}</p>}
        </div>

        {/* ================= RESULT CARD ================= */}
        {result && (
          <div className="card result-card">
            <h3>Candidate Match Result</h3>

            <div className="meta">
              <span>
                <b>Role:</b> {result.role}
              </span>
              <span className="score">{animatedScore}% Match</span>
            </div>

            {/* ===== Progress Bar ===== */}
            <div className="progress">
              <div
                className={`progress-fill ${getProgressClass(animatedScore)}`}
                style={{ width: `${animatedScore}%` }}
              />
            </div>

            {/* ===== Skills Section ===== */}
            <div className="skills-section">
              <p className="section-title">Matched Skills</p>
              <div className="chips matched">
                {result.matchedSkills?.length ? (
                  result.matchedSkills.map((s) => (
                    <span key={s}>{s}</span>
                  ))
                ) : (
                  <span className="empty">None</span>
                )}
              </div>

              {result.missingSkills?.length > 0 && (
                <>
                  <p className="section-title">Skills to Improve</p>
                  <div className="chips missing">
                    {result.missingSkills.map((s) => (
                      <span key={s}>{s}</span>
                    ))}
                  </div>
                </>
              )}
            </div>

            <p className="recommendation">
              <b>Recommendation:</b> {result.recommendation}
            </p>
          </div>
        )}
      </div>
    </div>
  );
}

export default ResumeUpload;
