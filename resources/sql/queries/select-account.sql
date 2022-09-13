select 
	a.ACCOUNT_ID 
	,a.NAME 
	,a.USER_ID
	,a.PASS 
from 
	conf.account a
	inner join conf.name n
	on a.ACCOUNT_ID = n.ACCOUNT_ID
	and n.PATH = ?
