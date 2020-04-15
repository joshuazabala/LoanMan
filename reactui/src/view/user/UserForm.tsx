import * as React from 'react';
import {
    Button,
    DropdownItemProps,
    DropdownOnSearchChangeData,
    DropdownProps,
    Form,
    FormButton,
    FormDropdown,
    FormInput,
    FormProps,
    InputOnChangeData,
    Message,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
} from 'semantic-ui-react';

import IFormProps from '../../common/IFormProps';
import IFormState from '../../common/IFormState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import { fetchPost } from '../../common/Request';
import Util from '../../common/Util';
import User from '../../model/User';
import UserProfile from '../../model/UserProfile';

interface IState extends IFormState<User> {
    userProfiles: DropdownItemProps[]
}

export default class UserForm extends React.Component<IFormProps, IState> {

    public state: IState = {
        content: new User(),
        errorMap: new Map<string, string>(),
        errorMessage: "",
        loading: false,
        userProfiles: new Array<DropdownItemProps>()
    }

    private profileSearchTimeout: number = 0;

    public componentDidMount() {
        const userProfiles = this.state.userProfiles;
        userProfiles.unshift({
            key: 0,
            value: 0,
            text: "Select Profile"
        });
    }

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(this.state.content.id === 0 ? "New" : "Update") + " User"}</ModalHeader>
                <ModalContent>
                    <Form onSubmit={this.onSubmit}>
                        <FormInput
                            label="Username"
                            value={this.state.content.username}
                            disabled={this.state.loading}
                            fieldname="username"
                            onChange={this.onInputChange}
                            error={this.state.errorMap.get("username")}
                            maxLength={32}
                        />
                        <FormDropdown
                            fluid={true}
                            label="Profile"
                            options={this.state.userProfiles}
                            value={this.state.content.profileId}
                            onChange={this.onProfileChange}
                            onSearchChange={this.onProfileSearch}
                            search={true}
                            selection={true}
                            onClick={this.onProfileSearchClick}
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("profileId")}
                        />
                        <FormInput
                            label="First Name"
                            value={this.state.content.firstName}
                            disabled={this.state.loading}
                            fieldname="firstName"
                            onChange={this.onInputChange}
                            error={this.state.errorMap.get("firstName")}
                            maxLength={32}
                        />
                        <FormInput
                            label="Middle Name"
                            value={this.state.content.middleName}
                            disabled={this.state.loading}
                            fieldname="middleName"
                            onChange={this.onInputChange}
                            maxLength={32}
                        />
                        <FormInput
                            label="Last Name"
                            value={this.state.content.lastName}
                            disabled={this.state.loading}
                            fieldname="lastName"
                            onChange={this.onInputChange}
                            error={this.state.errorMap.get("lastName")}
                            maxLength={32}
                        />
                        <FormInput
                            label="Contact No."
                            value={this.state.content.contactNumber}
                            disabled={this.state.loading}
                            fieldname="contactNumber"
                            onChange={this.onInputChange}
                            error={this.state.errorMap.get("contactNumber")}
                            maxLength={64}
                        />
                        <FormInput
                            label="Email Address"
                            value={this.state.content.emailAddress}
                            disabled={this.state.loading}
                            fieldname="emailAddress"
                            onChange={this.onInputChange}
                            error={this.state.errorMap.get("contactNumber")}
                            maxLength={64}
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
        this.props.onSaved();
    }

    private onInputChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const content = this.state.content;
        Reflect.set(content, data.fieldname, data.value);
        this.setState({ content });
    }

    private onProfileSearch = (event: React.SyntheticEvent<HTMLElement>, data: DropdownOnSearchChangeData) => {
        this.getProfiles(data.searchQuery);
    }

    private getProfiles = (queryString: string) => {
        clearTimeout(this.profileSearchTimeout);
        this.profileSearchTimeout = window.setTimeout(() => {
            const requestParam = new PagedSearchRequest();
            requestParam.includeInactive = false;
            requestParam.pageNumber = 1;
            requestParam.pageSize = 20;
            requestParam.queryString = queryString;

            fetchPost<PagedSearchRequest, PagedSearchResponse<UserProfile>>("/userprofile/search", requestParam)
                .then(result => {
                    const userProfiles = new Array<DropdownItemProps>();
                    result.content.forEach(item => {
                        userProfiles.push({
                            key: item.id,
                            value: item.id,
                            text: item.name
                        });
                    });
                    this.setState({ userProfiles });
                });
        }, 200);
    }

    private onProfileChange = (event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => {
        const content = this.state.content;
        content.profileId = data.value as number;
        this.setState({ content });
    }

    private onProfileSearchClick = (event: React.KeyboardEvent<HTMLElement>, data: DropdownProps) => {
        if (!event.currentTarget.firstChild) {
            return;
        }
        const node = (event.currentTarget.firstChild! as unknown) as { value: string };
        if (node !== undefined) {
            this.getProfiles(node.value);
        }
    }

}