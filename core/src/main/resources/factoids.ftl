<form action="/factoids" method="GET">
    <table class="factoids zebra-striped">
        <tr>
            <td class="right top" colspan="4">
                <div id="pagination" class="pagination">
                    <ul>
                        <li id="previousPage" class="prev <#if !previousPage?? >disabled</#if>">
                            <a <#if previousPage?? >href="${previousPage}"</#if>>&larr; Previous</a>
                        </li>

                        <li id="currentPage" class="current"> Displaying ${startRange} to ${endRange} of ${itemCount} </li>

                        <li id="nextPage" class="next <#if !nextPage?? >disabled</#if>">
                            <a <#if nextPage?? >href="${nextPage}"</#if>>Next &rarr;</a>
                        </li>
                    </ul>
                </div>
            </td>
        </tr>
        <tr>
            <th>Name</th>
            <th>Value</th>
            <th>Added By</th>
            <th class="right">Updated</th>
        </tr>
        <tr>
            <td><input type="text" name="name" value="<#if filter.name??>${filter.name}</#if>"></td>
            <td><input type="text" name="value" value="<#if filter.value??>${filter.value}</#if>"></td>
            <td><input type="text" name="userName" value="<#if filter.userName??>${filter.userName}</#if>"></td>
            <td class="right"><input type="submit" class="submit" name="Submit"></td>
        </tr>
    <#list getPageItems() as factoid>
        <tr>
            <td>
                <#if isAdmin()>
                    <a id="${factoid.id}" href="" class="locklink <#if factoid.locked>locked</#if>">&nbsp;</a>
                </#if>
            ${factoid.name}
            </td>
            <td>${factoid.value}</td>
            <td>${factoid.userName}</td>
            <td class="right">${format(factoid.updated)}</td>
        </tr>
    </#list>
    </table>
</form>