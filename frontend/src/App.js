import React, { useEffect, useState } from 'react';

function App() {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);

  // Configurable backend URL (important for ECS later)
  const backendUrl = process.env.REACT_APP_API_URL || "http://localhost:3000";

  useEffect(() => {
    fetch(`${backendUrl}/api/time`)
      .then((res) => {
        if (!res.ok) {
          throw new Error("Failed to reach backend");
        }
        return res.json();
      })
      .then((json) => setData(json))
      .catch((err) => setError(err.message));
  }, [backendUrl]);

  return (
    <div className="container">
      <h1>DevBank Microservices</h1>
      <h2>Welcome to DevBank</h2>

      {error && <p style={{ color: "red" }}>Error: {error}</p>}

      {data ? (
        <div>
          <p>{data.message}</p>
          <p>
            <strong>Server time:</strong> {data.time}
          </p>
        </div>
      ) : (
        !error && <p>Loading...</p>
      )}
    </div>
  );
}

export default App;
