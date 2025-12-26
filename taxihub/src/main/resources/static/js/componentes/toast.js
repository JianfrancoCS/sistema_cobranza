
const Toast = {
    show: function(message, type = 'info', title = 'Información', duration = 3000) {
        let toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            toastContainer = this.createContainer();
        }

        const toastElement = this.createToast(message, type, title, duration);
        toastContainer.appendChild(toastElement);

        const bsToast = new bootstrap.Toast(toastElement, {
            delay: duration
        });

        bsToast.show();

        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });

        return toastElement;
    },

    createContainer: function() {
        const container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '9999';
        document.body.appendChild(container);
        return container;
    },

    createToast: function(message, type, title, duration) {
        const toastId = 'toast-' + Date.now();
        const typeConfig = this.getTypeConfig(type);

        const toastHTML = `
            <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header ${typeConfig.headerClass}">
                    <i class="${typeConfig.icon} me-2"></i>
                    <strong class="me-auto">${title}</strong>
                    <small>ahora</small>
                    <button type="button" class="btn-close ${typeConfig.closeBtn}" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    ${message}
                </div>
            </div>
        `;

        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = toastHTML;
        return tempDiv.firstElementChild;
    },

    getTypeConfig: function(type) {
        const configs = {
            success: {
                icon: 'bi bi-check-circle-fill text-success',
                headerClass: 'bg-success text-white',
                closeBtn: 'btn-close-white'
            },
            error: {
                icon: 'bi bi-exclamation-triangle-fill text-danger',
                headerClass: 'bg-danger text-white',
                closeBtn: 'btn-close-white'
            },
            warning: {
                icon: 'bi bi-exclamation-triangle-fill text-warning',
                headerClass: 'bg-warning text-dark',
                closeBtn: ''
            },
            info: {
                icon: 'bi bi-info-circle-fill text-info',
                headerClass: 'bg-info text-white',
                closeBtn: 'btn-close-white'
            }
        };
        return configs[type] || configs.info;
    },

    success: function(message, title = 'Éxito', duration = 4000) {
        return this.show(message, 'success', title, duration);
    },

    error: function(message, title = 'Error', duration = 6000) {
        return this.show(message, 'error', title, duration);
    },

    warning: function(message, title = 'Advertencia', duration = 5000) {
        return this.show(message, 'warning', title, duration);
    },

    info: function(message, title = 'Información', duration = 4000) {
        return this.show(message, 'info', title, duration);
    },

    initAutoDetection: function() {
        document.addEventListener('DOMContentLoaded', function() {
            const successElement = document.querySelector('[data-success-message]');
            const errorElement = document.querySelector('[data-error-message]');

            if (successElement) {
                const message = successElement.getAttribute('data-success-message');
                if (message && message.trim() !== '') {
                    Toast.success(message);
                }
            }

            if (errorElement) {
                const message = errorElement.getAttribute('data-error-message');
                if (message && message.trim() !== '') {
                    Toast.error(message);
                }
            }

            if (typeof window.successMessage !== 'undefined' && window.successMessage) {
                Toast.success(window.successMessage);
            }

            if (typeof window.errorMessage !== 'undefined' && window.errorMessage) {
                Toast.error(window.errorMessage);
            }
        });
    }
};

window.Toast = Toast;

window.showToast = function(title, message, type = 'info') {
    return Toast.show(message, type, title);
};

Toast.initAutoDetection();