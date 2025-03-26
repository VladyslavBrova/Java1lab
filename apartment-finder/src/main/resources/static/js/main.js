document.addEventListener('DOMContentLoaded', function() {
    // Функція для форматування ціни
    function formatPrice(price) {
        return new Intl.NumberFormat('uk-UA').format(price);
    }

    // Функція для підсвічування елементів при наведенні
    const tableRows = document.querySelectorAll('tbody tr');
    if (tableRows) {
        tableRows.forEach(row => {
            row.addEventListener('mouseenter', function() {
                this.style.backgroundColor = '#f8f9fa';
            });

            row.addEventListener('mouseleave', function() {
                this.style.backgroundColor = '';
            });
        });
    }

    // Встановлення значення USD курсу
    const usdRateElement = document.querySelector('.badge.bg-info span');
    if (usdRateElement) {
        const usdRate = parseFloat(usdRateElement.textContent);

        // Функція для конвертації гривень в долари
        window.convertToUSD = function(uahAmount) {
            const usdAmount = uahAmount / usdRate;
            return Math.round(usdAmount);
        };

        // Функція для конвертації доларів в гривні
        window.convertToUAH = function(usdAmount) {
            const uahAmount = usdAmount * usdRate