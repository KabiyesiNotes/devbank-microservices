const express = require('express');
const path = require('path');
const app = express();
const PORT = process.env.PORT || 3000;
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:18080';

app.use(express.static(path.join(__dirname, 'build')));

app.get('/config.js', (req, res) => {
  res.type('application/javascript');
  res.send(`window.BACKEND_URL = "${BACKEND_URL}";`);
});

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'build', 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Frontend running on port ${PORT} → Backend: ${BACKEND_URL}`);
});
