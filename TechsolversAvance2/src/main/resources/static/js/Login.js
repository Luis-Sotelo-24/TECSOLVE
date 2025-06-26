document.getElementById('login').addEventListener('click', function () {
    const correo = document.getElementById('email').value.trim();
    const contraseña = document.getElementById('password').value;

    const params = new URLSearchParams({
        correo: correo,
        contraseña: contraseña
    });

    fetch('/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: params.toString()
    })
    .then(res => res.text())
    .then(data => {
        // Redirigir según el rol recibido del backend
        if (data === 'ok_admin') {
            window.location.href = '/inicio';  // Redirige al admin , antes era admin solo de prueba nomas 
        } else if (data === 'ok_user') {
            window.location.href = '/inicio';  // Redirige al usuario
        } else {
            alert('Correo o contraseña incorrectos');
        }
    })
    .catch(error => {
        alert('Error al iniciar sesión: ' + error.message);
    });
});