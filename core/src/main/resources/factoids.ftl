<form action="/factoids" method="GET">
    <table class="factoids zebra-striped">
        <tr>
            <td class="right top" colspan="4">
                <div id="pagination" class="pagination">
                    <ul>
                    <#if previousPage?? >
                        <li class="prev"><a href="/factoids?page=${previousPage}">&larr; Previous</a></li>
                    <#else>
                        <li class="prev disabled"><a>&larr; Previous</a></li>
                    </#if>
                        <li class="current">
                            Displaying ${startRange} to ${endRange} of ${itemCount}
                        </li>
                    <#if nextPage?? >
                        <li class="next"><a href="/factoids?page=${nextPage}">Next &rarr;</a></li>
                    <#else>
                        <li class="next disabled"><a>Next &rarr;</a></li>
                    </#if>
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