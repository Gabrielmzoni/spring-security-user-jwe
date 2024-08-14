package br.com.gmsoft.userjwe.mapper;


import br.com.gmsoft.userjwe.domain.User;
import br.com.gmsoft.userjwe.dto.UserDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );


    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);
}
