package models.bases;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import models.EventType;

@Entity
@Table(name = "logs")
public class LogsBase extends Model {
  public String nick;
  public String channel;
  public String message;
  public Date updated;
  public Type type;
  
}
