#!/bin/sh

# Health check script for Finance Frontend
# Checks if nginx is running and serving content

# Check if nginx process is running
if ! pgrep nginx > /dev/null; then
    echo "ERROR: nginx process not found"
    exit 1
fi

# Check if the health endpoint responds
if ! wget --no-verbose --tries=1 --spider http://localhost:8080/health 2>/dev/null; then
    echo "ERROR: Health endpoint not responding"
    exit 1
fi

# Check if main application is accessible
if ! wget --no-verbose --tries=1 --spider http://localhost:8080/ 2>/dev/null; then
    echo "ERROR: Main application not accessible"
    exit 1
fi

echo "OK: Finance Frontend is healthy"
exit 0
