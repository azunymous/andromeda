class Config {
    static getAPIURL() {
        return window['config']['apiUrl'] || process.env.REACT_APP_API_URL ||""
    }
}

export default Config
