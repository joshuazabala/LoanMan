import * as React from 'react';
import {
    Button,
    Checkbox,
    CheckboxProps,
    Form,
    FormButton,
    FormInput,
    FormProps,
    Grid,
    GridColumn,
    GridRow,
    Header,
    InputOnChangeData,
    Message,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
    Segment,
} from 'semantic-ui-react';

import { EnumModuleAccessId } from '../../common/EnumModuleAccessId';
import IFormProps from '../../common/IFormProps';
import IFormState from '../../common/IFormState';
import Util from '../../common/Util';
import UserProfile from '../../model/UserProfile';

interface IAccessIdMap {
    accessId: EnumModuleAccessId,
    label: string
}

interface IAccessIdGroup {
    parent: IAccessIdMap,
    members: IAccessIdMap[]
}

const AccessIdMapping: IAccessIdGroup[] = [
    // clients module
    {
        parent: { accessId: EnumModuleAccessId.CLIENT_VIEW, label: "Clients Module" },
        members: [
            { accessId: EnumModuleAccessId.CLIENT_CREATE, label: "Create clients" },
            { accessId: EnumModuleAccessId.CLIENT_EDIT, label: "Update clients" },
            { accessId: EnumModuleAccessId.CLIENT_DELETE, label: "Delete clients" },
            { accessId: EnumModuleAccessId.CLIENT_RESTORE, label: "Restore deleted clients" }
        ]
    },
    // groups module
    {
        parent: { accessId: EnumModuleAccessId.GROUP_VIEW, label: "Groups Module" },
        members: [
            { accessId: EnumModuleAccessId.GROUP_CREATE, label: "Create groups" },
            { accessId: EnumModuleAccessId.GROUP_EDIT, label: "Update groups" },
            { accessId: EnumModuleAccessId.GROUP_DELETE, label: "Delete groups" },
            { accessId: EnumModuleAccessId.GROUP_RESTORE, label: "Restore deleted groups" }
        ]
    },
    // group types module
    {
        parent: { accessId: EnumModuleAccessId.GROUP_TYPE_VIEW, label: "Group Types Module" },
        members: [
            { accessId: EnumModuleAccessId.GROUP_TYPE_CREATE, label: "Create group types" },
            { accessId: EnumModuleAccessId.GROUP_TYPE_EDIT, label: "Update group types" },
            { accessId: EnumModuleAccessId.GROUP_TYPE_DELETE, label: "Delete group types" },
            { accessId: EnumModuleAccessId.GROUP_TYPE_RESTORE, label: "Restore deleted group types" }
        ]
    },
    // loans module
    {
        parent: { accessId: EnumModuleAccessId.LOAN_VIEW, label: "Loans Module" },
        members: [
            { accessId: EnumModuleAccessId.LOAN_CREATE, label: "Create loans" },
            { accessId: EnumModuleAccessId.LOAN_EDIT, label: "Update loans" },
            { accessId: EnumModuleAccessId.LOAN_DELETE, label: "Delete loans" },
            { accessId: EnumModuleAccessId.LOAN_RESTORE, label: "Restore deleted loans" }
        ]
    },
    // loan types module
    {
        parent: { accessId: EnumModuleAccessId.LOAN_TYPE_VIEW, label: "Loan Types Module" },
        members: [
            { accessId: EnumModuleAccessId.LOAN_TYPE_CREATE, label: "Create loan types" },
            { accessId: EnumModuleAccessId.LOAN_TYPE_EDIT, label: "Update loan types" },
            { accessId: EnumModuleAccessId.LOAN_TYPE_DELETE, label: "Delete loan types" },
            { accessId: EnumModuleAccessId.LOAN_TYPE_RESTORE, label: "Restore deleted loan types" }
        ]
    },
    // cutoffs module
    {
        parent: { accessId: EnumModuleAccessId.CUTOFF_VIEW, label: "Cutoffs Module" },
        members: [
            { accessId: EnumModuleAccessId.CUTOFF_CREATE, label: "Create cutoffs" },
            { accessId: EnumModuleAccessId.CUTOFF_EDIT, label: "Update cutoffs" },
            { accessId: EnumModuleAccessId.CUTOFF_DELETE, label: "Delete cutoffs" },
            { accessId: EnumModuleAccessId.CUTOFF_RESTORE, label: "Restore deleted cutoffs" },
            { accessId: EnumModuleAccessId.CUTOFF_POST, label: "Post cutoffs" },
            { accessId: EnumModuleAccessId.CUTOFF_UNPOST, label: "Unpost cutoffs" }
        ]
    },
    // cutoff profiles module
    {
        parent: { accessId: EnumModuleAccessId.CUTOFF_PROFILE_VIEW, label: "Cutoff Profiles Module" },
        members: [
            { accessId: EnumModuleAccessId.CUTOFF_PROFILE_CREATE, label: "Create cutoff profiles" },
            { accessId: EnumModuleAccessId.CUTOFF_PROFILE_EDIT, label: "Update cutoff profiles" },
            { accessId: EnumModuleAccessId.CUTOFF_PROFILE_DELETE, label: "Delete cutoff profiles" },
            { accessId: EnumModuleAccessId.CUTOFF_PROFILE_RESTORE, label: "Restore deleted cutoff profiles" }
        ]
    },
    // users module
    {
        parent: { accessId: EnumModuleAccessId.USER_VIEW, label: "Users Module" },
        members: [
            { accessId: EnumModuleAccessId.USER_CREATE, label: "Create users" },
            { accessId: EnumModuleAccessId.USER_EDIT, label: "Update users" },
            { accessId: EnumModuleAccessId.USER_DELETE, label: "Delete users" },
            { accessId: EnumModuleAccessId.USER_RESTORE, label: "Restore deleted users" }
        ]
    },
    // cutoff profiles module
    {
        parent: { accessId: EnumModuleAccessId.USER_PROFILE_VIEW, label: "User Profiles Module" },
        members: [
            { accessId: EnumModuleAccessId.USER_PROFILE_CREATE, label: "Create user profiles" },
            { accessId: EnumModuleAccessId.USER_PROFILE_EDIT, label: "Update user profiles" },
            { accessId: EnumModuleAccessId.USER_PROFILE_DELETE, label: "Delete user profiles" },
            { accessId: EnumModuleAccessId.USER_PROFILE_RESTORE, label: "Restore deleted user profiles" }
        ]
    },
    // payments module
    {
        parent: { accessId: EnumModuleAccessId.PAYMENT_VIEW, label: "Payments Module" },
        members: [
            { accessId: EnumModuleAccessId.PAYMENT_CREATE, label: "Create payments" },
            { accessId: EnumModuleAccessId.PAYMENT_EDIT, label: "Update payments" },
            { accessId: EnumModuleAccessId.PAYMENT_DELETE, label: "Delete payments" },
            { accessId: EnumModuleAccessId.PAYMENT_RESTORE, label: "Restore deleted payments" }
        ]
    },
    // sessions module
    {
        parent: { accessId: EnumModuleAccessId.SESSION_VIEW, label: "Sessions Module" },
        members: [
            { accessId: EnumModuleAccessId.SESSION_VIEW_DETAILS, label: "View session details" },
            { accessId: EnumModuleAccessId.SESSION_DEACTIVATE, label: "Deactivate sessions" }
        ]
    },
];

export default class UserProfileForm extends React.Component<IFormProps, IFormState<UserProfile>> {

    public state: IFormState<UserProfile> = {
        content: new UserProfile(),
        loading: false,
        errorMap: new Map<string, string>(),
        errorMessage: ""
    }

    public render() {
        return (
            <Modal open={this.props.visible} size="small">
                <ModalHeader>{(this.state.content.id === 0 ? "New" : "Update") + " User Profile"}</ModalHeader>
                <ModalContent>
                    <Form onSubmit={this.onSubmit}>
                        <FormInput
                            label="Name"
                            value={this.state.content.name}
                            fieldname="name"
                            onChange={this.onInputChange}
                            maxLength="32"
                            disabled={this.state.loading}
                            error={this.state.errorMap.get("name")}
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
                    {/* client module */}
                    <Header as="h4">Module Access</Header>
                    {
                        AccessIdMapping.map((item, index) => {
                            return (
                                <Segment key={index}>
                                    <Grid>
                                        <GridRow columns={1}>
                                            <GridColumn>
                                                <Checkbox
                                                    accessid={item.parent.accessId}
                                                    label={item.parent.label}
                                                    toggle={true}
                                                    onClick={this.onCheckboxChange}
                                                    checked={this.hasAccessTo(item.parent.accessId)}
                                                />
                                            </GridColumn>
                                        </GridRow>
                                        <GridRow columns={2}>
                                            {
                                                item.members.map((member, memberIndex) => {
                                                    return (
                                                        <GridColumn key={memberIndex}>
                                                            <Checkbox
                                                                accessid={member.accessId}
                                                                parentaccessid={item.parent.accessId}
                                                                label={member.label}
                                                                toggle={true}
                                                                disabled={!this.hasAccessTo(item.parent.accessId)}
                                                                onClick={this.onCheckboxChange}
                                                                checked={this.hasAccessTo(member.accessId)}
                                                            />
                                                        </GridColumn>
                                                    );
                                                })
                                            }
                                        </GridRow>
                                    </Grid>
                                </Segment>
                            );
                        })
                    }
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
        if (Util.isBlankOrNullString(content.name)) {
            errorMap.set("name", "Name is required.");
        }
        if (Util.isBlankOrNullString(content.description)) {
            errorMap.set("description", "Description is required.");
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
        if (data.fieldname === "name") {
            content.name = data.value;
        } else if (data.fieldname === "description") {
            content.description = data.value;
        }
        this.setState({ content });
    }

    private hasAccessTo = (accessId: EnumModuleAccessId) => {
        return this.state.content.moduleAccessIds.findIndex(item => item === accessId) > -1;
    }

    private onCheckboxChange = (event: React.MouseEvent<HTMLInputElement, MouseEvent>, data: CheckboxProps) => {
        const accessid = data.accessid as EnumModuleAccessId;
        if (data.parentaccessid) {
            const parentaccessid = data.parentaccessid as EnumModuleAccessId;
            if (!this.hasAccessTo(parentaccessid)) {
                return;
            }
        }
        const content = this.state.content;
        if (data.checked) {
            const index = content.moduleAccessIds.findIndex(item => item === accessid);
            if (index === -1) {
                content.moduleAccessIds.unshift(accessid);
            } else {
                return;
            }
        } else {
            content.moduleAccessIds = content.moduleAccessIds.filter(item => item !== accessid);
        }
        this.setState({ content });
    }

}