<form action="/factoids" method="GET">
    <tr>
        <th>Message</th>
        <th class="right">Updated</th>
    </tr>
    <tr>
        <td><input type="text" name="message" value="<#if filter.name??>${filter.name}</#if>"></td>
        <td class="right"><input type="submit" class="submit" name="Submit"></td>
    </tr>
<#list getPageItems() as change>
    <tr>
        <td>${change.message}</td>
        <td class="right">${format(change.changeDate)}</td>
    </tr>
</#list>
</form>