package net.consensys.web3auth.common.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Organisation implements Serializable {

    private static final long serialVersionUID = -2161172913558756817L;
     
    private String name;
    private int role;
    private Date dateAdded;

    
    public Organisation(String name, int role) {
        super();
        this.name = name;
        this.role = role;
        this.dateAdded = new Date();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Organisation other = (Organisation) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (role != other.role)
            return false;
        return true;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + role;
        return result;
    }

}
