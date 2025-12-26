
document.addEventListener('DOMContentLoaded', function() {
    const currentPath = window.location.pathname;
    console.log('Sidebar.js loaded. Current path:', currentPath);

    function restoreSidebarState() {
        const savedState = localStorage.getItem('sidebarState');
        if (savedState) {
            const state = JSON.parse(savedState);
            Object.keys(state).forEach(accordionId => {
                if (state[accordionId]) {
                    const accordion = document.getElementById(accordionId);
                    if (accordion) {
                        const bsCollapse = new bootstrap.Collapse(accordion, { toggle: false });
                        bsCollapse.show();
                    }
                }
            });
        }
    }

    function saveSidebarState() {
        const accordions = ['empleadosAccordion', 'seguridadAccordion'];
        const state = {};

        accordions.forEach(accordionId => {
            const accordion = document.getElementById(accordionId);
            if (accordion) {
                state[accordionId] = accordion.classList.contains('show');
            }
        });

        localStorage.setItem('sidebarState', JSON.stringify(state));
    }

    function activateCurrentSection() {
        let targetAccordionId = null;

        if (currentPath.startsWith('/usuarios') || currentPath.startsWith('/autoridades') || currentPath.startsWith('/grupos')) {
            targetAccordionId = 'seguridadAccordion';
        } else if (currentPath.startsWith('/empleados')) {
            targetAccordionId = 'empleadosAccordion';
        }

        restoreSidebarState();

        if (targetAccordionId) {
            const targetAccordion = document.getElementById(targetAccordionId);
            if (targetAccordion && !targetAccordion.classList.contains('show')) {
                const bsCollapse = new bootstrap.Collapse(targetAccordion, { toggle: false });
                bsCollapse.show();
            }
        }

        markActiveLink();
    }

    function markActiveLink() {
        const sidebarLinks = document.querySelectorAll('.sidebar a[href], .sidebar .btn[href]');

        sidebarLinks.forEach(link => {
            link.classList.remove('active');
            const linkHref = link.getAttribute('href');
            if (linkHref === currentPath) {
                link.classList.add('active');
                console.log('Marked active link:', linkHref);
            }
        });
    }

    function addAccordionListeners() {
        const accordions = document.querySelectorAll('[data-bs-target^="#"]:not([data-bs-target="#sidebarAccordion"])');
        accordions.forEach(button => {
            button.addEventListener('click', () => {
                setTimeout(saveSidebarState, 350);
            });
        });
    }

    activateCurrentSection();
    addAccordionListeners();
});