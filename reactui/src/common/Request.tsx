export async function fetchPostFormData<OUTPUT_TYPE>(url: string, data: FormData): Promise<OUTPUT_TYPE> {
    const headers = {
        "Accept": "application/json"
    }
    const x = process.env.REACT_APP_API_URL + url;
    const response = await fetch(x, {
        body: data,
        headers,
        method: "POST"
    });
    return response.json();
}

export async function fetchGetNoReturn(url: string) {
    const headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    };
    const x = process.env.REACT_APP_API_URL + url;
    await fetch(x, {
        headers,
        method: "GET"
    });
}

export async function fetchGet<OUTPUT_TYPE>(url: string): Promise<OUTPUT_TYPE> {
    const headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    };
    const x = process.env.REACT_APP_API_URL + url;
    const response = await fetch(x, {
        headers,
        method: "GET"
    });
    return response.json();
}

export async function fetchPost<INPUT_TYPE, OUTPUT_TYPE>(url: string, data: INPUT_TYPE): Promise<OUTPUT_TYPE> {
    const headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    }
    const x = process.env.REACT_APP_API_URL + url;
    const response = await fetch(x, {
        body: JSON.stringify(data),
        headers,
        method: "POST"
    });
    return response.json();
}

export async function fetchPostWithFile<OUTPUT_TYPE>(url: string, data: FormData): Promise<OUTPUT_TYPE> {
    const headers = {
        "Accept": "application/json"
    }
    const response = await fetch(process.env.REACT_APP_API_URL + url, {
        body: data,
        headers,
        method: "POST"
    });
    return response.json();
}

export async function downloadPost<INPUT_TYPE>(url: string, data: INPUT_TYPE) {
    const headers = {
        "Accept": "application/json"
    }
    const response = await window.fetch(process.env.REACT_APP_API_URL + url, {
        body: JSON.stringify(data),
        credentials: "include",
        headers,
        method: "POST"
    });
    const result = response.blob();
    return result;
}