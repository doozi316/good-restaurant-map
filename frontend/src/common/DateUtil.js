export function utcDateStrToVisualLocalDateStr(utcDateStr)
{
    return dateToVisualDateStr(utcDateStrToLocalDate(utcDateStr));
}

export function utcDateStrToLocalDate(utcDateStr)
{
    const date = dateStrToDate(utcDateStr);
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    return date;
}

// YYYYMMDDHHMMSS
export function dateStrToDate(dateStr)
{
    return new Date(
        parseInt(dateStr.substring(0, 4)),
        parseInt(dateStr.substring(4, 6)) - 1,
        parseInt(dateStr.substring(6, 8)),
        parseInt(dateStr.substring(8, 10)),
        parseInt(dateStr.substring(10, 12)),
        parseInt(dateStr.substring(12, 14)));
}

export function dateToVisualDateStr(date)
{
    return `
        ${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()} 
        ${date.getHours()}:${date.getMinutes()}:${date.getSeconds()}
    `
}
