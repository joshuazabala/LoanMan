import * as React from 'react';
import {
    Button,
    ButtonProps,
    Container,
    Dropdown,
    DropdownItemProps,
    DropdownOnSearchChangeData,
    DropdownProps,
    Form,
    FormInput,
    FormProps,
    Grid,
    GridColumn,
    GridRow,
    Header,
    Icon,
    InputOnChangeData,
    Loader,
    Modal,
    ModalActions,
    ModalContent,
    ModalHeader,
    Pagination,
    PaginationProps,
    Table,
    TableBody,
    TableCell,
    TableFooter,
    TableHeader,
    TableHeaderCell,
    TableRow,
} from 'semantic-ui-react';

import { EnumCommonAction } from '../../common/EnumCommonAction';
import { EnumResponseStatus } from '../../common/EnumResponseStatus';
import IPageState from '../../common/IPageState';
import PagedSearchRequest from '../../common/PagedSearchRequest';
import PagedSearchResponse from '../../common/PagedSearchResponse';
import PageStat from '../../common/PageStat';
import { fetchGet, fetchGetNoReturn, fetchPost } from '../../common/Request';
import ResponseContainer from '../../common/ResponseContainer';
import Util from '../../common/Util';
import User from '../../model/User';
import UserProfile from '../../model/UserProfile';
import DeleteRestoreDialog from '../common/DeleteRestoreDialog';
import FilterDialog from '../common/FilterDialog';
import UserForm from './UserForm';

interface IState extends IPageState<User> {
    deleteRestoreModalVisible: boolean,
    filterDialogVisible: boolean,
    profiles: DropdownItemProps[],
    selectedProfileId: number,
    resetPasswordDialogVisible: boolean
}

export default class UserPage extends React.Component<any, IState> {

    public state: IState = {
        contents: new Array<User>(),
        deleteRestoreModalVisible: false,
        formVisible: false,
        initialized: false,
        loading: false,
        name: "userPage",
        pageStat: new PageStat(),
        queryString: "",
        selectedId: 0,
        filterDialogVisible: false,
        profiles: new Array<DropdownItemProps>(),
        selectedProfileId: 0,
        resetPasswordDialogVisible: false
    }

    private formRef = React.createRef<UserForm>();
    private deleteRestoreDialogRef = React.createRef<DeleteRestoreDialog>();
    private filterDialogRef = React.createRef<FilterDialog>();
    private profileSearchTimeout: number = 0;

    public componentDidMount() {
        if (!this.state.initialized) {
            const pageStat = this.state.pageStat;
            pageStat.columnSorting.set("username", "ascending");
            const profiles = this.state.profiles;
            profiles.unshift({
                key: 0,
                value: 0,
                text: "All Profiles"
            });
            this.setState(
                { initialized: true, pageStat, profiles },
                () => {
                    this.search();
                }
            );
        }
    }

    public render() {
        let selectedActive = true;
        if (this.state.selectedId !== 0) {
            selectedActive = this.state.contents.filter(item => item.id === this.state.selectedId)[0].active;
        }
        return (
            <Container fluid={true}>
                <Grid>
                    <GridRow style={{ paddingBottom: 2 }}>
                        <GridColumn>
                            <Header as="h2">Users</Header>
                        </GridColumn>
                    </GridRow>
                    <GridRow columns={3} style={{ paddingBottom: 2, paddingTop: 2 }}>
                        <GridColumn width={6}>
                            <Button icon="add" primary={true} content="New" disabled={this.state.loading} onClick={this.onAdd} />
                            <Button icon="edit" primary={true} content="Edit" disabled={this.state.loading || this.state.selectedId === 0} onClick={this.onEdit} />
                            {
                                selectedActive &&
                                <Button
                                    icon="trash"
                                    negative={true}
                                    content="Delete"
                                    disabled={this.state.loading || this.state.selectedId === 0}
                                    action={EnumCommonAction.DELETE}
                                    onClick={this.onDeleteOrRestore}
                                />
                            }
                            {
                                !selectedActive &&
                                <Button
                                    icon="undo"
                                    positive={true}
                                    content="Restore"
                                    disabled={this.state.loading || this.state.selectedId === 0}
                                    action={EnumCommonAction.RESTORE}
                                    onClick={this.onDeleteOrRestore}
                                />
                            }
                            <Button 
                                icon="key"
                                primary={true}
                                content="Reset Password"
                                disabled={this.state.loading || this.state.selectedId === 0}
                                onClick={this.onResetPasswordClick}
                            />
                        </GridColumn>
                        <GridColumn width={5}>
                            <Dropdown
                                fluid={true}
                                options={this.state.profiles}
                                value={this.state.selectedProfileId}
                                selection={true}
                                disabled={this.state.loading}
                                onChange={this.onSelectedProfileChanged}
                                search={true}
                                onSearchChange={this.onProfileSearchChanged}
                                onClick={this.onProfileSearchClick}
                            />
                        </GridColumn>
                        <GridColumn textAlign="right" width={5}>
                            <Form onSubmit={this.onFormSubmit}>
                                <FormInput
                                    disabled={this.state.loading}
                                    placeholder="Search"
                                    value={this.state.queryString}
                                    onChange={this.onQueryStringChanged}
                                >
                                    <input style={{ borderTopRightRadius: 0, borderBottomRightRadius: 0 }} />
                                    <Button icon="search" content="Search" primary={true} style={{ borderTopLeftRadius: 0, borderBottomLeftRadius: 0 }} />
                                    <Button icon="cog" type="button" onClick={this.showFilterDialog} />
                                </FormInput>
                            </Form>
                        </GridColumn>
                    </GridRow>
                    <GridRow style={{ paddingBottom: 40, paddingTop: 2 }}>
                        <GridColumn>
                            <Table celled={true} striped={true} selectable={true} sortable={true}>
                                <TableHeader>
                                    <TableRow>
                                        <TableHeaderCell data-columnname="username" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("username")} width={3}>Username</TableHeaderCell>
                                        <TableHeaderCell data-columnname="name" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("name")} width={4}>Name</TableHeaderCell>
                                        <TableHeaderCell data-columnname="profile.name" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("profile.name")} width={3}>Profile</TableHeaderCell>
                                        <TableHeaderCell data-columnname="contactNumber" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("contactNumber")} width={3}>Contact No.</TableHeaderCell>
                                        <TableHeaderCell data-columnname="emailAddress" onClick={this.onColumnSort} sorted={this.state.pageStat.columnSorting.get("emailAddress")} width={3}>Email Address</TableHeaderCell>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {
                                        this.state.contents.map((item, index) => {
                                            return (
                                                <TableRow key={index} data-id={item.id} onClick={this.onRowClick} active={this.state.selectedId === item.id} negative={!item.active}>
                                                    <TableCell>{item.username}</TableCell>
                                                    <TableCell>{item.lastName + ", " + item.firstName + (Util.isBlankOrNullString(item.middleName) ? "" : " " + item.middleName)}</TableCell>
                                                    <TableCell>{item.profile}</TableCell>
                                                    <TableCell>{item.contactNumber}</TableCell>
                                                    <TableCell>{item.emailAddress}</TableCell>
                                                </TableRow>
                                            )
                                        })
                                    }
                                </TableBody>
                                <TableFooter>
                                    <TableRow>
                                        <TableHeaderCell colSpan={5} textAlign="right">
                                            <Loader active={this.state.loading} />
                                            <Pagination
                                                disabled={this.state.loading}
                                                onPageChange={this.onPageChange}
                                                defaultActivePage={this.state.pageStat.currentPage}
                                                totalPages={this.state.pageStat.totalPageCount}
                                                ellipsisItem={{ content: <Icon name='ellipsis horizontal' />, icon: true }}
                                                firstItem={{ content: <Icon name='angle double left' />, icon: true }}
                                                lastItem={{ content: <Icon name='angle double right' />, icon: true }}
                                                prevItem={{ content: <Icon name='angle left' />, icon: true }}
                                                nextItem={{ content: <Icon name='angle right' />, icon: true }}
                                            />
                                        </TableHeaderCell>
                                    </TableRow>
                                </TableFooter>
                            </Table>
                        </GridColumn>
                    </GridRow>

                    <UserForm
                        visible={this.state.formVisible}
                        ref={this.formRef}
                        onCancelled={this.onFormCancelled}
                        onSaved={this.onFormSaved}
                    />

                    <DeleteRestoreDialog
                        onCancel={this.onCancelDeleteOrRestore}
                        onDelete={this.onDelete}
                        onRestore={this.onRestore}
                        open={this.state.deleteRestoreModalVisible}
                        size="small"
                        ref={this.deleteRestoreDialogRef}
                    />

                    <FilterDialog
                        onClose={this.onFilterDialogClose}
                        onReset={this.onFilterDialogReset}
                        onSaveAndSearch={this.onFilterDialogSaveAndSearch}
                        open={this.state.filterDialogVisible}
                        ref={this.filterDialogRef}
                        size="small"
                    />

                    <Modal size="small" open={this.state.resetPasswordDialogVisible}>
                        <ModalHeader>Confirm Action</ModalHeader>
                        <ModalContent>Reset user password?</ModalContent>
                        <ModalActions>
                            <Button 
                                content="Reset Password"
                                icon="key"
                                primary={true}
                                onClick={this.resetPassword}
                            />
                            <Button 
                                content="Cancel"
                                icon="cancel"
                                onClick={this.cancelResetPassword}
                            />
                        </ModalActions>
                    </Modal>

                </Grid>
            </Container>
        );
    }

    private onRowClick = (event: any) => {
        const userId = event.currentTarget.getAttribute("data-id") as number;
        this.setState({ selectedId: +userId });
    }

    private onQueryStringChanged = (event: React.ChangeEvent<HTMLInputElement>, data: InputOnChangeData) => {
        this.setState({ queryString: data.value });
    }

    private onFormSubmit = (event: React.FormEvent<HTMLFormElement>, data: FormProps) => {
        event.preventDefault();
        const pageStat = this.state.pageStat;
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private onPageChange = (event: React.MouseEvent<HTMLElement, MouseEvent>, data: PaginationProps) => {
        const pageStat = this.state.pageStat;
        pageStat.currentPage = data.activePage as number;
        this.setState(
            { pageStat, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private search = () => {
        const requestParam = new PagedSearchRequest();
        requestParam.includeInactive = this.state.pageStat.showInactive;
        requestParam.pageNumber = this.state.pageStat.currentPage;
        requestParam.pageSize = this.state.pageStat.pageSize;
        requestParam.queryString = this.state.queryString;
        requestParam.otherData = { profileId: this.state.selectedProfileId };
        // if sort by name is present, replace with lastName, firstName, middleName
        const columnSorting = this.state.pageStat.columnSorting;
        if (columnSorting.has("name")) {
            const order = columnSorting.get("name");
            columnSorting.delete("name");
            columnSorting.set("lastName", order);
            columnSorting.set("firstName", order);
            columnSorting.set("middleName", order);
        }
        requestParam.columnSorting = Util.columnSortingMapToObject(columnSorting);

        this.setState(
            {
                loading: true
            },
            () => {
                fetchPost<PagedSearchRequest, PagedSearchResponse<User>>("/user/search", requestParam)
                    .then(result => {
                        if (result.status === EnumResponseStatus.SUCCESSFUL) {
                            const pageStat = this.state.pageStat;
                            pageStat.totalPageCount = result.totalPageCount;
                            pageStat.columnSorting = Util.objectToColumnSortingMap(result.columnSorting);
                            if (pageStat.columnSorting.has("lastName")) {
                                const order = pageStat.columnSorting.get("lastName");
                                pageStat.columnSorting.delete("lastName");
                                pageStat.columnSorting.delete("firstName");
                                pageStat.columnSorting.delete("middleName");
                                pageStat.columnSorting.set("name", order);
                            }
                            this.setState({
                                contents: result.content,
                                loading: false,
                                pageStat
                            });
                        } else {
                            this.setState(
                                { loading: false },
                                () => {
                                    alert("Error: " + result.message);
                                }
                            );
                        }
                    });
            }
        );
    }

    private showForm = (id: number) => {
        fetchGet<User>("/user/findById/" + id)
            .then(item => {
                this.setState(
                    { formVisible: true },
                    () => {
                        const userProfiles = [];
                        userProfiles.unshift({
                            key: item.profileId,
                            value: item.profileId,
                            text: item.profile
                        });
                        this.formRef.current!.setState({ 
                            content: item, 
                            userProfiles,
                            errorMap: new Map<string, string>()
                        });
                    }
                );
            });
    }

    private onFormCancelled = () => {
        this.setState({ formVisible: false });
    }

    private onFormSaved = () => {
        this.formRef.current!.setState(
            { loading: true },
            () => {
                const content = this.formRef.current!.state.content;

                const url = "/user/" + (content.id === 0 ? "create" : "edit");
                fetchPost<User, ResponseContainer<User>>(url, content)
                    .then(response => {
                        this.formRef.current!.setState(
                            { loading: false },
                            () => {
                                if (response.status === EnumResponseStatus.SUCCESSFUL) {
                                    const contents = this.state.contents;
                                    const index = contents.findIndex(item => item.id === response.content.id);
                                    if (index === -1) {
                                        contents.unshift(response.content);
                                    } else {
                                        contents[index] = response.content;
                                    }

                                    this.setState({
                                        formVisible: false,
                                        contents
                                    });
                                } else {
                                    this.formRef.current!.setState({
                                        loading: false,
                                        errorMap: Util.objectToMap(response.errorMap),
                                        errorMessage: response.message
                                    });
                                }
                            }
                        );
                    });
            }
        );
    }

    private onAdd = () => {
        this.showForm(0);
    }

    private onEdit = () => {
        if (this.state.selectedId === 0) {
            return
        }

        this.showForm(this.state.selectedId);
    }

    private onDelete = () => {
        const id = this.deleteRestoreDialogRef.current!.state.id;
        this.deleteOrRestore(EnumCommonAction.DELETE, id as number);
    }

    private onDeleteOrRestore = (event: React.MouseEvent<HTMLButtonElement, MouseEvent>, data: ButtonProps) => {
        const action = data.action as EnumCommonAction;
        this.setState(
            { deleteRestoreModalVisible: true },
            () => {
                this.deleteRestoreDialogRef.current!.setState({
                    header: "Confirm Action",
                    id: this.state.selectedId,
                    message: (action === EnumCommonAction.RESTORE ? "Restore " : "Delete ") + "selected user profile?",
                    mode: action === EnumCommonAction.RESTORE ? EnumCommonAction.RESTORE : EnumCommonAction.DELETE
                });
            }
        );
    }

    private showFilterDialog = () => {
        const pageStat = this.state.pageStat;
        this.setState(
            { filterDialogVisible: true },
            () => {
                this.filterDialogRef.current!.setState({
                    itemsPerPage: pageStat.pageSize,
                    showInactive: pageStat.showInactive
                });
            }
        );
    }

    private onColumnSort = (event: any) => {
        const columnname = event.currentTarget.getAttribute("data-columnname") as string;
        const pageStat = this.state.pageStat;
        const currentOrder = pageStat.columnSorting.get(columnname);
        const newOrder = currentOrder === undefined ? "ascending" : currentOrder === "ascending" ? "descending" : currentOrder === "descending" ? undefined : undefined;
        pageStat.columnSorting.set(columnname, newOrder);
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private onCancelDeleteOrRestore = () => {
        this.setState({ deleteRestoreModalVisible: false });
    }

    private onRestore = () => {
        const id = this.deleteRestoreDialogRef.current!.state.id;
        this.deleteOrRestore(EnumCommonAction.RESTORE, id as number);
    }

    private deleteOrRestore = (mode: EnumCommonAction.DELETE | EnumCommonAction.RESTORE, id: number) => {
        const url = "/user/" + (mode === EnumCommonAction.RESTORE ? "restore" : "delete") + "/" + id;
        this.setState(
            { loading: true },
            () => {
                fetchGetNoReturn(url)
                    .then(() => {
                        let contents = this.state.contents;
                        if (mode === EnumCommonAction.RESTORE) {
                            const index = contents.findIndex(item => item.id === id);
                            contents[index].active = true;
                        } else {
                            if (this.state.pageStat.showInactive) {
                                contents.find(item => item.id === id)!.active = false;
                            } else {
                                contents = contents.filter(item => item.id !== this.state.selectedId);
                            }
                        }
                        this.setState({
                            contents: contents,
                            selectedId: 0,
                            deleteRestoreModalVisible: false,
                            loading: false
                        });
                    });
            }
        );
    }

    private onFilterDialogClose = () => {
        this.setState({ filterDialogVisible: false });
    }

    private onFilterDialogReset = () => {
        this.filterDialogRef.current!.setState({
            itemsPerPage: 20,
            showInactive: false
        });
    }

    private onFilterDialogSaveAndSearch = () => {
        const filterState = this.filterDialogRef.current!.state;
        const pageStat = this.state.pageStat;
        pageStat.pageSize = filterState.itemsPerPage;
        pageStat.showInactive = filterState.showInactive;
        pageStat.currentPage = 1;
        this.setState(
            { pageStat, filterDialogVisible: false, selectedId: 0 },
            () => {
                this.search();
            }
        );
    }

    private onSelectedProfileChanged = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownProps) => {
        this.setState(
            { selectedProfileId: data.value as number },
            () => {
                const pageStat = this.state.pageStat;
                pageStat.currentPage = 1;
                this.setState(
                    { pageStat, selectedId: 0 },
                    () => {
                        this.search();
                    }
                );
            }
        );
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
                    const profiles = new Array<DropdownItemProps>();
                    profiles.push({
                        key: 0,
                        value: 0,
                        text: "All Profiles"
                    });
                    result.content.forEach(item => {
                        profiles.push({
                            key: item.id,
                            value: item.id,
                            text: item.name
                        });
                    });
                    this.setState({ profiles });
                });
        }, 200);
    }

    private onProfileSearchChanged = (event: React.SyntheticEvent<HTMLElement, Event>, data: DropdownOnSearchChangeData) => {
        this.getProfiles(data.searchQuery);
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

    private onResetPasswordClick = () => {
        this.setState({ resetPasswordDialogVisible: true });
    }

    private cancelResetPassword = () => {
        this.setState({ resetPasswordDialogVisible: false });
    }

    private resetPassword = () => {
        fetchGetNoReturn("/user/resetPassword/" + this.state.selectedId)
        .then(() => {
            this.setState({ resetPasswordDialogVisible: false });
        });
    }

}