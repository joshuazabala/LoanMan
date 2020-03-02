import * as React from 'react';
import {
    Button,
    DropdownItemProps,
    DropdownOnSearchChangeData,
    DropdownProps,
    Form,
    FormButton,
    FormDropdown,
    FormGroup,
    FormInput,
    FormProps,
    FormTextArea,
    InputOnChangeData,
    Message,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
    TextAreaProps,
} from 'semantic-ui-react';

import { EnumLoanStatus } from '../../common/EnumLoanStatus';
import IFormProps from '../../common/IFormProps';
import IFormState from '../../common/IFormState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import { fetchPost } from '../../common/Request';
import Util from '../../common/Util';
import Client from '../../model/Client';
import Loan from '../../model/Loan';
import LoanType from '../../model/LoanType';

interface IState extends IFormState<Loan> {
    loanTypes: DropdownItemProps[],
    clients: DropdownItemProps[],
}

export default class LoanForm extends React.Component<IFormProps, IState> {

    public state: IState = {
        content: new Loan(),
        errorMap: new Map<string, string>(),
        errorMessage: "",
        loading: false,
        loanTypes: new Array<DropdownItemProps>(),
        clients: new Array<DropdownItemProps>()
    }

    public componentDidMount() {
        this.getLoanTypes("");
    }

    private groupTypeSearchTimeout: number = 0;
    private clienteSearchTimeout: number = 0;

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(this.state.content.id === 0 ? "New" : "Update") + " Loan"}</ModalHeader>
                <ModalContent>
                    <Form onSubmit={this.onSubmit}>
                        {
                            this.state.content.id === 0 &&
                            <FormDropdown
                                fluid={true}
                                label="Client"
                                options={this.state.clients}
                                value={this.state.content.clientId}
                                onChange={this.onClientChange}
                                onSearchChange={this.onClientSearch}
                                search={true}
                                selection={true}
                                onClick={this.onClientSearchClick}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("clientId")}
                            />
                        }
                        {
                            this.state.content.id !== 0 &&
                            <FormInput
                                label="Client"
                                readOnly={true}
                                value={this.state.content.client}
                            />
                        }
                        {
                            this.state.content.id === 0 &&
                            <FormDropdown
                                fluid={true}
                                label="Loan Type"
                                options={this.state.loanTypes}
                                value={this.state.content.loanTypeId}
                                onChange={this.onLoanTypeChange}
                                onSearchChange={this.onLoanTypeSearch}
                                search={true}
                                selection={true}
                                onClick={this.onLoanTypeSearchClick}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("loanTypeId")}
                            />
                        }
                        {
                            this.state.content.id !== 0 &&
                            <FormInput 
                                label="Loan Type"
                                readOnly={true}
                                value={this.state.content.loanType}
                            />
                        }
                        <FormGroup widths="equal">
                            <FormInput
                                type="number"
                                label="Principal"
                                value={this.state.content.principal}
                                fieldname="principal"
                                onChange={this.onNumericInputChange}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("principal")}
                            />
                            <FormInput
                                type="number"
                                label="Payable"
                                value={this.state.content.payable}
                                fieldname="payable"
                                onChange={this.onNumericInputChange}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("payable")}
                            />
                            <FormInput
                                type="number"
                                label="Amortization"
                                value={this.state.content.amortization}
                                fieldname="amortization"
                                onChange={this.onNumericInputChange}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("amortization")}
                            />
                        </FormGroup>
                        <FormGroup widths="equal">
                            <FormInput
                                type="date"
                                label="Loan Date"
                                value={this.state.content.loanDate}
                                fieldname="loanDate"
                                onChange={this.onDateChange}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("loanDate")}
                            />
                            <FormInput
                                type="date"
                                label="Payment Start Date"
                                value={this.state.content.paymentStartDate}
                                fieldname="paymentStartDate"
                                onChange={this.onDateChange}
                                disabled={this.state.loading}
                                error={this.state.errorMap.get("paymentStartDate")}
                            />
                        </FormGroup>
                        <FormTextArea
                            label="Remarks"
                            value={this.state.content.remarks}
                            onChange={this.onRemarksChange}
                            disabled={this.state.loading}
                        />
                        {
                            !Util.isBlankOrNullString(this.state.errorMessage) &&
                            <Message error={true}>
                                {this.state.errorMessage}
                            </Message>
                        }
                        <FormButton
                            style={{ display: "none" }}
                        />
                    </Form>
                </ModalContent>
                <ModalActions>
                    <Button
                        primary={true}
                        content={this.state.content.id === 0 ? "Create" : "Update"}
                        icon="save"
                        onClick={this.onSave}
                        loading={this.state.loading}
                        disabled={this.state.loading}
                    />
                    <Button
                        negative={true}
                        content="Cancel"
                        icon="cancel"
                        onClick={this.props.onCancelled}
                        disabled={this.state.loading}
                    />
                </ModalActions>
            </Modal>
        );
    }

    public onSubmit = (event: React.FormEvent<HTMLFormElement>, data: FormProps) => {
        event.preventDefault();
        this.onSave();
    }

    private onSave = () => {
        const content = this.state.content;
        const errorMap = new Map<string, string>();
        if (content.amortization <= 0) {
            errorMap.set("amortization", "Invalid amount.");
        }
        if (Util.isBlankOrNullString(content.clientId)) {
            errorMap.set("clientId", "This field is required.");
        }
        if (content.loanDate === undefined) {
            errorMap.set("loanDate", "Loan date is required.");
        }
        if (content.loanTypeId === 0) {
            errorMap.set("loanTypeId", "This field is required.");
        }
        if (content.payable <= 0) {
            errorMap.set("payable", "Invalid amount.");
        }
        if (content.paymentStartDate === undefined) {
            errorMap.set("paymentStartDate", "This field is required.");
        }
        if (content.principal <= 0) {
            errorMap.set("principal", "Invalid amount.");
        }
        if (content.status === EnumLoanStatus.UNKNOWN) {
            errorMap.set("status", "This field is required.");
        }
        this.setState(
            { errorMap },
            () => {
                if (errorMap.size === 0) {
                    this.props.onSaved();
                }
            }
        );
    }

    private onRemarksChange = (event: React.FormEvent<HTMLTextAreaElement>, data: TextAreaProps) => {
        const content = this.state.content;
        content.remarks = data.value as string;
        this.setState({ content });
    }

    private onDateChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const content = this.state.content;
        Reflect.set(content, data.fieldname as string, data.value);
        this.setState({ content });
    }

    private onNumericInputChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const content = this.state.content;
        Reflect.set(content, data.fieldname as string, data.value);
        this.setState({ content });
    }

    private onClientChange = (event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => {
        const content = this.state.content;
        content.clientId = data.value as string;
        this.setState({ content });
    }

    private onLoanTypeChange = (event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => {
        const content = this.state.content;
        content.loanTypeId = data.value as number;
        this.setState({ content });
    }

    private onClientSearch = (event: React.SyntheticEvent<HTMLElement>, data: DropdownOnSearchChangeData) => {
        this.getClients(data.searchQuery);
    }

    private onLoanTypeSearch = (event: React.SyntheticEvent<HTMLElement>, data: DropdownOnSearchChangeData) => {
        this.getLoanTypes(data.searchQuery);
    }

    private getClients = (queryString: string) => {
        clearTimeout(this.clienteSearchTimeout);
        this.clienteSearchTimeout = window.setTimeout(() => {
            const requestParam = new PagedSearchRequest();
            requestParam.includeInactive = false;
            requestParam.pageNumber = 1;
            requestParam.pageSize = 20;
            requestParam.queryString = queryString;

            fetchPost<PagedSearchRequest, PagedSearchResponse<Client>>("/client/search", requestParam)
                .then(result => {
                    const clients = new Array<DropdownItemProps>();
                    result.content.forEach(item => {
                        clients.push({
                            key: item.id,
                            value: item.id,
                            text: item.lastName + ", " + item.firstName + (Util.isBlankOrNullString(item.middleName) ? "" : " " + item.middleName)
                        });
                    });
                    this.setState({ clients });
                });
        }, 200);
    }

    private getLoanTypes = (queryString: string) => {
        clearTimeout(this.groupTypeSearchTimeout);
        this.groupTypeSearchTimeout = window.setTimeout(() => {
            const requestParam = new PagedSearchRequest();
            requestParam.includeInactive = false;
            requestParam.pageNumber = 1;
            requestParam.pageSize = 20;
            requestParam.queryString = queryString;

            fetchPost<PagedSearchRequest, PagedSearchResponse<LoanType>>("/loantype/search", requestParam)
                .then(result => {
                    const loanTypes = new Array<DropdownItemProps>();
                    result.content.forEach(item => {
                        loanTypes.push({
                            key: item.id,
                            value: item.id,
                            text: item.code + " - " + item.description
                        });
                    });
                    this.setState({ loanTypes });
                });
        }, 200);
    }

    private onClientSearchClick = (event: React.KeyboardEvent<HTMLElement>, data: DropdownProps) => {
        if (!event.currentTarget.firstChild) {
            return;
        }
        const node = (event.currentTarget.firstChild! as unknown) as { value: string };
        if (node !== undefined) {
            this.getClients(node.value);
        }
    }

    private onLoanTypeSearchClick = (event: React.KeyboardEvent<HTMLElement>, data: DropdownProps) => {
        if (!event.currentTarget.firstChild) {
            return;
        }
        const node = (event.currentTarget.firstChild! as unknown) as { value: string };
        if (node !== undefined) {
            this.getLoanTypes(node.value);
        }
    }

}