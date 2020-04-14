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
import Group from '../../model/Group';
import GroupType from '../../model/GroupType';

interface IState extends IFormState<Group> {
    groupTypes: DropdownItemProps[]
}

export default class GroupForm extends React.Component<IFormProps, IState> {

    public state: IState = {
        content: new Group(),
        loading: false,
        errorMap: new Map<string, string>(),
        errorMessage: "",
        groupTypes: new Array<DropdownItemProps>()
    }

    public componentDidMount() {
        this.getGroupTypes("");
    }

    private groupTypeSearchTimeout: number = 0;

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(this.state.content.id === 0 ? "New" : "Update") + " Group"}</ModalHeader>
                <ModalContent>
                    <Form onSubmit={this.onSubmit}>
                        <FormDropdown 
                            fluid={true}
                            label="Group Type"
                            options={this.state.groupTypes}
                            value={this.state.content.groupTypeId}
                            onChange={this.onGroupTypeChange}
                            onSearchChange={this.onGroupTypeSearch}
                            search={true}
                            selection={true}
                            onClick={this.onGroupTypeSearchClick}
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("groupTypeId")}
                        />
                        <FormInput
                            label="Code"
                            value={this.state.content.code}
                            fieldname="code"
                            onChange={this.onInputChange}
                            maxLength="16"
                            width={8}
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("code")}
                        />
                        <FormInput
                            label="Description"
                            value={this.state.content.description}
                            onChange={this.onInputChange}
                            fieldname="description"
                            maxLength="512"
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("description")}
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
        if (Util.isBlankOrNullString(content.code)) {
            errorMap.set("code", "Code is required.");
        }
        if (Util.isBlankOrNullString(content.description)) {
            errorMap.set("description", "Description is required.");
        }
        if (content.groupTypeId === 0) {
            errorMap.set("groupTypeId", "Type is required.");
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

    private onInputChange = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        const content = this.state.content;
        if (data.fieldname === "code") {
            content.code = data.value;
        } else if (data.fieldname === "description") {
            content.description = data.value;
        }
        this.setState({ content });
    }

    private onGroupTypeChange = (event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => {
        const content = this.state.content;
        content.groupTypeId = data.value as number;
        this.setState({ content });
    }

    private onGroupTypeSearch = (event: React.SyntheticEvent<HTMLElement>, data: DropdownOnSearchChangeData) => {
        this.getGroupTypes(data.searchQuery);
    }

    private getGroupTypes = (queryString: string) => {
        clearTimeout(this.groupTypeSearchTimeout);
        this.groupTypeSearchTimeout = window.setTimeout(() => {
            const requestParam = new PagedSearchRequest();
            requestParam.includeInactive = false;
            requestParam.pageNumber = 1;
            requestParam.pageSize = 20;
            requestParam.queryString = queryString;

            fetchPost<PagedSearchRequest, PagedSearchResponse<GroupType>>("/grouptype/search", requestParam)
            .then(result => {
                const groupTypes = new Array<DropdownItemProps>();
                result.content.forEach(item => {
                    groupTypes.push({
                        key: item.id,
                        value: item.id,
                        text: item.name + " - " + item.description
                    });
                });
                this.setState({ groupTypes });
            });
        }, 200);
    }

    private onGroupTypeSearchClick = (event: React.KeyboardEvent<HTMLElement>, data: DropdownProps) => {
        if (!event.currentTarget.firstChild) {
            return;
        }
        const node = (event.currentTarget.firstChild! as unknown) as { value: string };
        if (node !== undefined) {
            this.getGroupTypes(node.value);
        }
    }

}