import { type Ref } from 'vue';

export const useAmountFormat = () => {
  const formatAmount = value => {
    if (value === null || value === undefined || value === '') {
      return '';
    }
    const num = parseFloat(value);
    return isNaN(num) ? '' : num.toFixed(2);
  };

  /**
   * Parse a formatted amount string back to a number
   * @param {string} value - The formatted string to parse
   * @returns {number|null} Parsed number or null if invalid
   */
  const parseAmount = value => {
    if (value === null || value === undefined || value === '') {
      return null;
    }
    const num = parseFloat(value);
    return isNaN(num) ? null : num;
  };

  /**
   * Format a number with thousand separators and 2 decimal places
   * @param {number|string} value - The value to format
   * @returns {string} Formatted number with thousand separators and 2 decimal places
   */
  const formatAmountWithSeparator = value => {
    if (value === null || value === undefined || value === '') {
      return '';
    }
    const num = parseFloat(value);
    if (isNaN(num)) {
      return '';
    }
    return num.toLocaleString('en-US', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });
  };

  const baseUtils = {
    formatAmount,
    parseAmount,
    formatAmountWithSeparator,
  };

  return {
    ...baseUtils,
  };
};

// Default export for backward compatibility
export default useAmountFormat;
