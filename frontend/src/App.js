import React, { useEffect, useState } from 'react';
function App() {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const backendUrl = process.env.REACT_APP_API_URL || "http://localhost:3002";
  useEffect(() => {
    fetch(`${backendUrl}/api/time`)
     .then(r => { if(!r.ok) throw new Error('Backend unreachable'); return r.json(); })
     .then(setData)
     .catch(e => setError(e.message));
  }, [backendUrl]);
  return (
    <div className="container">
      <h1>DevBank Microservices</h1>
      <h2>Welcome to DevBank - V1</h2>
      <p style={{fontSize:'12px'}}>Backend URL: {backendUrl}</p>
      {error && <p style={{color:'red'}}>Error: {error}</p>}
      {data? (
        <div>
          <p><b>{data.message}</b></p>
          <p>Server time: {data.time}</p>
          <p>Version: {data.version}</p>
        </div>
      ) : (!error && <p>Loading...</p>)}
    </div>
  );
}
export default App;
