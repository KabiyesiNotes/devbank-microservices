const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());

app.get('/api/time', (req, res) => {
  const now = new Date().toISOString();
  res.json({
    message: "Hello from DevBank Backend",
    time: now
  });
});

app.get('/', (req, res) => {
  res.send("DevBank Backend is running");
});

app.listen(PORT, () => {
  console.log(`Backend running on port ${PORT}`);
});
