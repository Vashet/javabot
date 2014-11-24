@helper.form(action = routes.AdminController.addAdmin()) {
    <table class="form">
        <tr class="topRow">
            <td><label for="irc">@Sofia.ircName()</label></td>
            <td class="right"><input type="text" id="irc" name="ircName"></td>
        </tr>
        <tr class="topRow">
            <td><label for="host">@Sofia.hostName()</label></td>
            <td class="right"><input type="text" id="host" name="hostName"></td>
        </tr>
        <tr>
            <td><label for="userName">User Name</label></td>
            <td class="right"><input type="text" id="userName" name="userName"></td>
        </tr>
        <tr>
            <td colspan="2" class="form-submit right">
                <input type="submit" value="Submit">
            </td>
        </tr>
    </table>
}
<table>
    <tr>
        <th class="top">IRC Name</th>
        <th class="top">Host Name</th>
        <th class="top">User Name</th>
        <th class="top">Added By</th>
        <th class="top">Added On</th>
        <th class="top right">Action</th>
    </tr>
    @for(admin <- admins) {
        <tr>
            <td>
                @{admin.getIrcName}
            </td>
            <td>
                @{admin.getHostName}
            </td>
            <td>@{admin.getUserName}</td>
            <td>@{admin.getAddedBy}</td>
            <td class="right">@{DateTimeFormatter.ofPattern("yyyy-MM-dd").format(admin.getUpdated)}</td>
            <td class="right top">
                @if(!admin.getBotOwner) {
                    <a href="@{routes.AdminController.deleteAdmin(admin.getId.toString)}">
                        <img src="@routes.Assets.at("/images/boomy/delete24.png")" alt="Delete"/>
                    </a>
                }
            </td>
        </tr>
    }
</table>
}
