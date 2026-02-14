import React, { useEffect, useState } from "react";
import axios from "axios";
import "./ResumeUpload.css";

function ResumeUpload() {
  const [file, setFile] = useState(null);
  const [role, setRole] = useState("");
  const [experience, setExperience] = useState("");
  const [roles, setRoles] = useState([]);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/resume/roles")
      .then((res) => setRoles(res.data))
      .catch(() => setError("Failed to load roles"));
  }, []);

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
        "http://localhost:8080/api/resume/upload",
        formData
      );

      setResult(res.data);
    } catch {
      setError("Resume analysis failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="container">
        {/* UPLOAD CARD */}
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

        {/* RESULT CARD */}
        {result && (
          <div className="card result-card">
            <h3>Candidate Match Result</h3>

            <div className="meta">
              <span><b>Role:</b> {result.role}</span>
              <span className="score">{result.score}% Match</span>
            </div>

            {/* SCORE BAR */}
            <div className="progress">
              <div
                className={`progress-fill ${
                  result.score >= 70
                    ? "good"
                    : result.score >= 40
                    ? "avg"
                    : "bad"
                }`}
                style={{ width: `${result.score}%` }}
              />
            </div>

            {/* SKILLS */}
            <div className="skills-section">
              <p className="section-title">Matched Skills</p>
              <div className="chips matched">
                {result.matchedSkills?.length
                  ? result.matchedSkills.map((s) => (
                      <span key={s}>{s}</span>
                    ))
                  : <span className="empty">None</span>}
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
