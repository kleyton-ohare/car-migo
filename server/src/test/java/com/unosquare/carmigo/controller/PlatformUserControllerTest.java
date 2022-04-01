package com.unosquare.carmigo.controller;

import com.flextrade.jfixture.FixtureAnnotations;
import com.flextrade.jfixture.JFixture;
import com.flextrade.jfixture.annotations.Fixture;
import com.unosquare.carmigo.dto.CreatePlatformUserDTO;
import com.unosquare.carmigo.dto.GrabPlatformUserDTO;
import com.unosquare.carmigo.entity.PlatformUser;
import com.unosquare.carmigo.entity.UserAccessStatus;
import com.unosquare.carmigo.model.response.PlatformUserViewModel;
import com.unosquare.carmigo.service.PlatformUserService;
import com.unosquare.carmigo.util.ResourceUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PlatformUserControllerTest
{
    private static final String API_LEADING = "/v1/users/";
    private static final String POST_PLATFORM_USER_VALID_JSON =
            ResourceUtility.generateStringFromResource("requestJson/PlatformUserValid.json");
    private static final String POST_PLATFORM_USER_INVALID_JSON =
            ResourceUtility.generateStringFromResource("requestJson/PlatformUserInvalid.json");

    private MockMvc mockMvc;

    @Mock
    private ModelMapper modelMapperMock;
    @Mock
    private PlatformUserService platformUserServiceMock;

    @Fixture
    private GrabPlatformUserDTO grabPlatformUserDTOFixture;
    @Fixture
    private PlatformUserViewModel platformUserViewModelFixture;
    @Fixture
    private CreatePlatformUserDTO createPlatformUserDTOFixture;
    @Fixture
    private PlatformUser platformUserFixture;
    @Fixture
    private UserAccessStatus userAccessStatusFixture;

    @BeforeEach
    public void setUp()
    {
        final JFixture jFixture = new JFixture();
        jFixture.customise().circularDependencyBehaviour().omitSpecimen();
        FixtureAnnotations.initFixtures(this, jFixture);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new PlatformUserController(modelMapperMock, platformUserServiceMock))
                .build();
    }

    @Test
    public void get_PlatformUser_By_Id_Returns_PlatformUserViewModel() throws Exception
    {
        when(platformUserServiceMock.getPlatformUserById(anyInt())).thenReturn(grabPlatformUserDTOFixture);
        when(modelMapperMock.map(grabPlatformUserDTOFixture, PlatformUserViewModel.class))
                .thenReturn(platformUserViewModelFixture);

        mockMvc.perform(get(API_LEADING + anyInt())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(platformUserServiceMock).getPlatformUserById(anyInt());
    }

    @Test
    public void post_PlatformUser_Returns_HttpStatus_Created() throws Exception
    {
        mockMvc.perform(post(API_LEADING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(POST_PLATFORM_USER_VALID_JSON))
                .andExpect(status().isCreated());
        verify(platformUserServiceMock).createPlatformUser(any());
    }

    @Test
    public void post_PlatformUser_Returns_HttpStatus_Conflict() throws Exception
    {
        mockMvc.perform(post(API_LEADING)
                        .content(POST_PLATFORM_USER_INVALID_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(platformUserServiceMock, times(0)).createPlatformUser(any());
    }

    @Test
    public void delete_PlatformUser_Returns_HttpStatus_No_Content() throws Exception
    {
        doNothing().when(platformUserServiceMock).deletePlatformUserById(anyInt());
        mockMvc.perform(delete(API_LEADING + anyInt()))
                .andExpect(status().isNoContent());
        verify(platformUserServiceMock).deletePlatformUserById(anyInt());
    }
}
