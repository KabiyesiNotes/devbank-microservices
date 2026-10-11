'use strict';

const express = require('express');
const path    = require('path');
const http    = require('http');
const https   = require('https');

const app = express();

// -----------------------------------------------------------------------
// Configuration — BACKEND_URL is injected as an environment variable.
// Docker Compose sets:  BACKEND_URL=http://backend:8080
// AWS ECS will set the appropriate service-discovery or ALB endpoint.
// -----------------------------------------------------------------------
const BACKEND_URL = process.env.BACKEND_URL || 'http://backend:8080';
const PORT        = process.env.PORT        || 3000;

// Serve static files from the public/ directory
app.use(express.static(path.join(__dirname, 'public')));

// -----------------------------------------------------------------------
// Server-side proxy — /api/proxy/*
//
// The browser calls  GET /api/proxy/inventory  on this Express server.
// Express fetches    GET <BACKEND_URL>/api/inventory  internally.
// The response JSON is piped back to the browser.
//
// This means the browser only ever talks to the frontend container's
// hostname/port (localhost:3001 locally, the ALB/ECS DNS on AWS).
// The Docker-internal hostname "backend" is never exposed to the browser,
// so the same image works locally, in Docker Compose, and on AWS ECS
// without any environment variable changes.
// -----------------------------------------------------------------------
app.get('/api/proxy/*', (req, res) => {
  // Strip the /api/proxy prefix, keep /api/... so backend paths are preserved
  // e.g. /api/proxy/inventory  →  /api/inventory
  const backendPath = '/api/' + req.path.replace(/^\/api\/proxy\//, '');
  const targetUrl   = `${BACKEND_URL}${backendPath}`;

  const client = targetUrl.startsWith('https') ? https : http;

  client.get(targetUrl, (backendRes) => {
    res.status(backendRes.statusCode);
    // Forward content-type so the browser parses JSON correctly
    const ct = backendRes.headers['content-type'];
    if (ct) res.set('Content-Type', ct);
    backendRes.pipe(res);
  }).on('error', (err) => {
    console.error(`Proxy error fetching ${targetUrl}:`, err.message);
    res.status(502).json({
      error: 'Bad Gateway',
      message: `Could not reach backend at ${targetUrl}: ${err.message}`
    });
  });
});

// -----------------------------------------------------------------------
// Config endpoint — still available for diagnostics / AWS use.
// On AWS ECS the browser can reach the backend ALB directly, so the
// config endpoint remains useful as an alternative path.
// -----------------------------------------------------------------------
app.get('/config', (req, res) => {
  res.json({ backendUrl: BACKEND_URL });
});

// Health check for the frontend container itself
app.get('/health', (req, res) => {
  res.json({ status: 'healthy', service: 'inventory-frontend' });
});

// Catch-all: serve the SPA index for any unmatched route
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`DevBank Frontend listening on port ${PORT}`);
  console.log(`Backend URL configured as: ${BACKEND_URL}`);
});
