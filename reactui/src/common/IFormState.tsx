export default interface IFormState<T> {
    
    content: T;
    loading: boolean;
    errorMessage: string;
    errorMap: Map<string, string>;

}