
class MarcacionScanner {
    constructor() {
        this.scanner = null;
        this.isScanning = false;
        this.cooldownActive = false;
        this.cooldownTimer = null;

        this.config = {
            token: document.getElementById('dynamosoft-token').value,
            tipoMarcacion: document.getElementById('tipo-marcacion').value === 'true',
            apiUrl: 'http://localhost:8090/marcacion',
            cooldownSeconds: 3
        };

        this.init();
    }

    async init() {
        try {
            await this.initializeScanner();
            await this.startScanning();
            this.setupEventListeners();
        } catch (error) {
            console.error('Error inicializando scanner:', error);
            this.showToast('Error de Scanner', 'No se pudo iniciar el scanner.', 'error');
        }
    }

    async initializeScanner() {
        if (!this.config.token) {
            throw new Error('Token de Dynamsoft no encontrado');
        }

        try {
            const scannerConfig = {
                license: this.config.token,
                engineResourcePaths: {
                    rootDirectory: "https://cdn.jsdelivr.net/npm/"
                },
                uiPath: "https://cdn.jsdelivr.net/npm/dynamsoft-barcode-reader-bundle@11.0.6000/dist/ui/barcode-scanner.ui.xml",
                container: document.getElementById('barcode-scanner-container'),
                scanMode: Dynamsoft.EnumScanMode.SM_MULTI_UNIQUE,
                autoStartCapturing: true,
                showUploadImageButton: false,
                onUniqueBarcodeScanned: (result) => {
                    this.handleBarcodeDetected(result.text, result);
                }
            };

            this.scanner = new Dynamsoft.BarcodeScanner(scannerConfig);

        } catch (error) {
            throw new Error('Error configurando Dynamsoft Scanner: ' + error.message);
        }
    }

    async startScanning() {
        if (!this.scanner) {
            throw new Error('Scanner no inicializado');
        }

        try {
            await this.scanner.launch();
            this.isScanning = true;
        } catch (error) {
            throw new Error('Error iniciando escaneo: ' + error.message);
        }
    }

    async stopScanning() {
        if (this.scanner && this.isScanning) {
            try {
                await this.scanner.destroy();
                this.isScanning = false;
            } catch (error) {
                console.error('Error deteniendo scanner:', error);
            }
        }
    }

    async handleBarcodeDetected(barcodeText, result) {
        console.log('QR detectado:', barcodeText, result);

        if (this.cooldownActive) {
            console.log('Cooldown activo, ignorando lectura');
            return;
        }

        const numeroDocumento = this.validateBarcodeText(barcodeText);
        if (!numeroDocumento) {
            this.showToast('Código QR inválido', 'El código QR no contiene un número de documento válido', 'error');
            return;
        }

        this.activateCooldown();

        try {
            await this.procesarMarcacion(numeroDocumento);
        } catch (error) {
            console.error('Error procesando marcación:', error);
        }
    }

    validateBarcodeText(text) {
        const cleaned = text.trim();

        if (/^\d{8,}$/.test(cleaned)) {
            return cleaned;
        }

        return null;
    }

    async procesarMarcacion(numeroDocumento) {
        try {
            const url = `${this.config.apiUrl}/${numeroDocumento}?tipo_marcacion=${this.config.tipoMarcacion}`;

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });

            if (response.ok) {
                const responseData = await response.json();

                if (responseData.exito) {
                    this.showToast('Marcación exitosa', responseData.mensaje, 'success');
                } else {
                    this.showToast('Error en marcación', responseData.mensaje, 'error');
                }
            } else {
                this.showToast('Error de servidor', `Error HTTP ${response.status}`, 'error');
            }
        } catch (error) {
            console.error('Error en petición:', error);
            this.showToast('Error de conexión', 'No se pudo conectar con el servidor', 'error');
        }
    }

    activateCooldown() {
        this.cooldownActive = true;
        
        if (this.cooldownTimer) {
            clearTimeout(this.cooldownTimer);
        }

        this.cooldownTimer = setTimeout(() => {
            this.cooldownActive = false;
        }, this.config.cooldownSeconds * 1000);
    }

    showToast(title, message, type = 'info') {
        if (typeof window.showToast === 'function') {
            window.showToast(title, message, type);
        } else {
            alert(`${title}: ${message}`);
        }
    }


    setupEventListeners() {
        const btnCerrar = document.getElementById('btn-cerrar-scanner');
        if (btnCerrar) {
            btnCerrar.addEventListener('click', async () => {
                await this.stopScanning();
                window.location.href = '/';
            });
        }

        const btnPagos = document.getElementById('btn-ir-pagos');
        if (btnPagos) {
            btnPagos.addEventListener('click', () => {
                window.location.href = '/pagos';
            });
        }

        window.addEventListener('beforeunload', () => {
            this.cleanup();
        });
    }

    cleanup() {
        if (this.cooldownTimer) {
            clearTimeout(this.cooldownTimer);
        }

        if (this.scanner) {
            this.scanner.destroy().catch(console.error);
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    if (typeof Dynamsoft === 'undefined') {
        console.error('Dynamsoft SDK no está disponible');
        alert('Error: El componente de scanner no se pudo cargar.');
        return;
    }

    try {
        new MarcacionScanner();
    } catch (error) {
        console.error('Error inicializando MarcacionScanner:', error);
    }
});

window.MarcacionScanner = MarcacionScanner;
