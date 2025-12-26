document.addEventListener('DOMContentLoaded', function() {
    initTooltips();
});

function initTooltips() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}


function showConfirmModal(modalId, title, message, action, confirmText = 'Confirmar', isDanger = false) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    
    const titleElement = modal.querySelector('.modal-title span');
    const messageElement = modal.querySelector('.modal-body p');
    const form = modal.querySelector('form');
    const confirmButton = modal.querySelector('.btn-primary, .btn-danger');
    
    if (titleElement) titleElement.textContent = title;
    if (messageElement) messageElement.innerHTML = message;
    if (form) form.action = action;
    if (confirmButton) {
        confirmButton.textContent = confirmText;
        confirmButton.className = `btn ${isDanger ? 'btn-danger' : 'btn-primary'}`;
    }
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
}

function confirmDelete(button) {
    console.log('=== DEBUG confirmDelete ===');
    console.log('Button element:', button);
    console.log('Button dataset:', button.dataset);

    const id = button.dataset.id;
    const type = button.dataset.type;
    const name = button.dataset.name || '';

    console.log('ID extraído:', id);
    console.log('Type extraído:', type);
    console.log('Name extraído:', name);

    const action = `/${type}/eliminar/${id}`;
    console.log('Action construida:', action);

    const nameText = name ? ` <strong>${name}</strong>` : '';
    const message = `¿Está seguro que desea eliminar${nameText ? ' a' + nameText : ' este registro'}?`;

    const modal = document.getElementById('confirmModal');
    console.log('Modal encontrado:', modal);

    if (!modal) {
        console.error('ERROR: Modal no encontrado!');
        return;
    }

    const titleElement = modal.querySelector('.modal-title span');
    const messageElement = modal.querySelector('.modal-body p');
    const form = modal.querySelector('#confirmForm');
    const confirmButton = modal.querySelector('form button[type="submit"]');

    console.log('Form encontrado:', form);
    console.log('Form action será:', action);

    if (titleElement) titleElement.textContent = 'Confirmar Eliminación';
    if (messageElement) messageElement.innerHTML = message;
    if (form) {
        form.action = action;
        console.log('Form action asignada:', form.action);
    }
    if (confirmButton) {
        confirmButton.innerHTML = '<i class="bi bi-trash me-1"></i>Eliminar';
        confirmButton.className = 'btn btn-danger';
    }

    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
    console.log('=== FIN DEBUG ===');
}
function updateFormAction(formId, action) {
    const form = document.getElementById(formId);
    if (form) {
        form.action = action;
    }
}