package com.ino.myblog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(
		uniqueConstraints = {
				@UniqueConstraint(
						name="subscribe_uk",
						columnNames = {"fromUserId", "toUserId"}
				)
		}
)
public class Subscribe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@JoinColumn(name = "fromUserId") // 이렇게 컬럼명 만들어! 니 맘대로 만들지 말고!!
	@ManyToOne
	private User fromUser;
	
	@JoinColumn(name = "toUserId")
	@ManyToOne
	private User toUser;
	
	private LocalDateTime createDate;
	
	@PrePersist
	public void createDate() {
		this.createDate = LocalDateTime.now();
	}

}



