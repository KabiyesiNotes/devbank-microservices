import React, { useEffect, useState } from 'react';

function App() {
  const [data, setData] = useState(null);
  const backendUrl = window.BACKEND_URL || 'http://localhost:18080';

  useEffect(() => {
    fetch(`${backendUrl}/api/time`)
      .then(r => r.json())
      .then(setData)
      .catch(e => setData({ error: e.message }));
  }, [backendUrl]);

  return (
    <div style={{ padding: 20, fontFamily: 'Arial, sans-serif' }}>
      <h1>DevBank Microservices</h1>
      <h2>Frontend V1 (Runtime Config)</h2>
      <p><strong>Backend URL:</strong> {backendUrl}</p>
      <pre style={{ background: '#f4f4f4', padding: 15, borderRadius: 8 }}>
        {JSON.stringify(data, null, 2)}
      </pre>
    </div>
  );
}

export default App;
