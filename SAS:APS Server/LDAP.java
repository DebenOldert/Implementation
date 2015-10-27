/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 */

import java.util.HashMap;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


/**
 *
 * @author Deben Oldert
 */
public class LDAP {
    private static DirContext ctx;
    private static String userName;
    private static String passWord;
    private static String base = "dc=vpn,dc=local";

    LDAP(String username, String password) throws NamingException {
        Hashtable<String, String> env = new Hashtable();
        
        env.put(LdapContext.CONTROL_FACTORIES, "com.sun.jndi.ldap.ControlFactory");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://192.168.2.240:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "VPN\\Administrator");
        env.put(Context.SECURITY_CREDENTIALS, "Magnesium12");
        
        try {
            ctx = new InitialDirContext(env);
        }
        catch(NamingException e) {
            int errCode= getErrorCode(e.getMessage());
            System.out.println("*** Conection with LDAP failed ("+username+", "+password+") Error: "+errCode+" ***");
        }
        userName = username;
        passWord = password;
    }
    
    public int userCheck() throws NamingException {
        /*Hashtable tmpEnv = (Hashtable) ctx.getEnvironment().clone();
        
        tmpEnv.put(Context.SECURITY_PRINCIPAL, userName);
        tmpEnv.put(Context.SECURITY_CREDENTIALS, passWord);
        
        try {
            new InitialDirContext(tmpEnv);
            return 0;
        }
        catch(NamingException e) {
            return getErrorCode(e.toString()) + 700;
        }*/
        try {
            String[] key = {"cn"};

            SearchControls cons = new SearchControls();
            cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
            cons.setReturningAttributes(key);
            NamingEnumeration<SearchResult> answer = ctx.search(base, "cn=" + userName, cons);
            if(answer.hasMore()) {
                return 0;
            }
            else {
                return 730;
            }
        }
        catch(NamingException e) {
                return getErrorCode(e.toString()) + 700;
                }
        
    }
    
    public HashMap getUserInfo(String[] keys) throws NamingException {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        cons.setReturningAttributes(keys);
        
        HashMap<String, String> array = new HashMap();
        NamingEnumeration<SearchResult> answer = ctx.search(base, "cn=" + userName, cons);
        
        if(answer.hasMore()) {
            Attributes attrs = answer.next().getAttributes();
            
            String tmp;
            for(String key : keys) {
                if(attrs.get(key) != null) {
                    tmp = attrs.get(key).toString();
                    array.put(key, tmp.substring(tmp.indexOf(":") + 2));
                }
                else {
                    array.put(key, null);
                }
            }
            return array;
        }
        return null;
        
    }
    public boolean writeInfo(String key, String value) {
        System.out.println("Modding: "+key+", "+value);
        ModificationItem[] mods = new ModificationItem[1];
        String name = "CN="+userName+",CN=Users,DC=vpn,DC=local";
        System.out.println(name);
        
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
          new BasicAttribute(key, value));
        try {
            ctx.modifyAttributes(name, mods);
        }
        catch(NamingException e){
            System.out.println(e);
            return false;
        }
        return true;
    }
    public boolean deleteInfo(String key) {
        ModificationItem[] mods = new ModificationItem[1];
        String name = "CN="+userName+",CN=Users,DC=vpn,DC=local";
        System.out.println("unregistering: "+key);
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,new BasicAttribute(key));
        try {
            ctx.modifyAttributes(name, mods);
        }
        catch(NamingException e){
            System.out.println(e);
            return false;
        }
        return true;
    }
    
    private int getErrorCode(final String exceptionMsg)
    {
        String pattern="-?\\d+";
        Pattern p=Pattern.compile(pattern);
        Matcher  m=p.matcher(exceptionMsg);
        if (m.find()) {
            return Integer.valueOf(m.group(0));
        }
        return -1;
    }

}
