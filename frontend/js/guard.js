(function() {
    const API_BASE_URL = 'http://localhost:8080';
    const currentPage = window.location.pathname;

    // FIX: Update to match actual file names
    const pageRoles = {
        '/auditeur.html': ['AUDITEUR'],
        '/rssi-dashboard.html': ['RSSI', 'USER'],
        '/admin.html': ['ADMIN']
    };

    const authFlowPages = ['/qrcode.html', '/verify-otp.html', '/login.html', '/signup.html', '/index.html', '/'];
    if (authFlowPages.includes(currentPage)) {
        return; // No guard needed for public pages
    }

    function redirectToDefaultDashboard(role) {
        if (role === 'ADMIN') { window.location.href = '/admin.html'; }
        else if (role === 'RSSI' || role === 'USER') { window.location.href = '/rssi-dashboard.html'; }
        else if (role === 'AUDITEUR') { window.location.href = '/auditeur.html'; }
        else { window.location.href = '/login.html'; }
    }

    // 1. Check if token exists locally
    const token = localStorage.getItem('authToken');
    if (!token) {
        console.error('Token manquant, redirection vers login.');
        window.location.href = '/login.html';
        return;
    }

    // 2. Optional: Verify token with backend (or just decode JWT locally)
    // NOTE: Ensure your AuthController has an endpoint GET /auth/me or /users/me

    fetch(`${API_BASE_URL}/auth/users/me`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`, // SEND THE TOKEN!
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) { throw new Error('Non authentifié'); }
        return response.json();
    })
    .then(user => {
        const userRole = user.role; // Ensure backend returns "role"

        // Fix: Handle leading slash issues in paths
        const matchedPage = Object.keys(pageRoles).find(page => currentPage.endsWith(page));
        const allowedRoles = matchedPage ? pageRoles[matchedPage] : null;

        if (allowedRoles && !allowedRoles.includes(userRole)) {
            console.warn(`Accès refusé. Rôle: ${userRole}`);
            redirectToDefaultDashboard(userRole);
        } else {
            // Update UI with user info if element exists
            const userNameElement = document.getElementById('userName');
            if (userNameElement) {
                userNameElement.textContent = user.name || user.email;
            }
        }
    })
    .catch(error => {
        console.error('Session invalide:', error);
        localStorage.removeItem('authToken'); // Clean up bad token
        window.location.href = '/login.html';
    });

})();