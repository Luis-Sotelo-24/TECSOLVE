 document.getElementById('register').addEventListener('click', function () {
    // Limpiar mensajes de error previos
    document.getElementById('error-dni').textContent = '';
    document.getElementById('error-telefono').textContent = '';
    document.getElementById('error-password').textContent = '';

    // Crear objeto cliente con los datos del formulario
    const cliente = {
        nombre_cli: document.getElementById('username').value.trim(),
        apellidos_cli: document.getElementById('lastname').value.trim(),
        correo: document.getElementById('email').value.trim(),
        contraseña: document.getElementById('password').value,
        dni: document.getElementById('dni').value.trim(),
        direccion: document.getElementById('direccion').value.trim(),
        telefono: document.getElementById('telefono').value.trim()
    };

    const confirmPassword = document.getElementById('confirmPassword').value;
    let valido = true;

    // Validaciones personalizadas
    if (!/^\d{8}$/.test(cliente.dni)) {
        document.getElementById('error-dni').textContent = "El DNI debe tener exactamente 8 dígitos.";
        valido = false;
    }

    if (!/^\d{9}$/.test(cliente.telefono)) {
        document.getElementById('error-telefono').textContent = "El teléfono debe tener exactamente 9 dígitos.";
        valido = false;
    }

    if (cliente.contraseña !== confirmPassword) {
        document.getElementById('error-password').textContent = "Las contraseñas no coinciden.";
        valido = false;
    }

   
    for (const key in cliente) {
        if (!cliente[key]) {
            Swal.fire({
                icon: 'error',
                title: '¡Error!',
                text: 'Por favor, completa todos los campos.'
            });
            return;
        }
    }

    if (!valido) return;

    
    if (cliente.correo === 'admin@admin.com') {
        cliente.rol = 'ROLE_ADMIN';
    } else {
        cliente.rol = 'ROLE_USER';
    }

   
    fetch('/registro', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(cliente)
    })
    .then(response => response.text())
    .then(data => {
        if (data === 'ok') {
            Swal.fire({
                icon: 'success',
                title: '¡Registro Exitoso!',
                text: 'Ahora puedes iniciar sesión.'
            }).then(() => {
                window.location.href = 'login';
            });
        } else if (data === 'correo_exist') {
            Swal.fire({
                icon: 'error',
                title: '¡Error!',
                text: 'El correo ya está registrado.'
            });
        } else {
            Swal.fire({
                icon: 'error',
                title: '¡Error!',
                text: 'Error al registrar. Inténtalo de nuevo.'
            });
        }
    })
    .catch(error => {
        Swal.fire({
            icon: 'error',
            title: '¡Error!',
            text: 'Hubo un error en la conexión. Intenta nuevamente.'
        });
    });
});
